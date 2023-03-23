package da.nzse.engine;

import static da.nzse.engine.XMLCommon.XMLSkip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class LevelSegment {

    private Sprite mSprite;
    private ArrayList<Collider> mGeometryColliders;
    private ArrayList<Collider> mDeathColliders;
    private dVec2D mPosition;

    public LevelSocket leftSocket;
    public LevelSocket rightSocket;

    private static int maxID = 0;
    private int mGeometryID = 0;
    /// <summary>
    /// Creates a new LevelSegment from aSprite and a File with Collider Definitions
    /// </summary>
    /// <param name="sprite">the Sprite Object for the LevelSegment</param>
    /// <param name="collidersourcePath">the Path to the Collider Definition</param>
    public LevelSegment(Sprite sprite, XmlPullParser parser) {
        mGeometryID = maxID++;
        mGeometryColliders = new ArrayList<>();
        mDeathColliders = new ArrayList<>();

        mSprite = sprite;
        mPosition = new dVec2D(0,0);

        ArrayList<Polygon> levelGeometry;
        try {
            levelGeometry = Collider.readPolygons(parser);
            for(Polygon polygon : levelGeometry){
                mGeometryColliders.add(new Collider(polygon));
            }
        } catch (Exception E) {
            E.printStackTrace();
        }

    }

    /// <summary>
    /// Reads a LevelSegment from an XML file
    /// </summary>
    /// <param name="parser">the XML parser to read from</param>
    /// <param name="context">the Context to read the resource from</param>
    public LevelSegment(XmlPullParser parser, Context context)
    throws XmlPullParserException, IOException {
        mGeometryID = maxID++;
        mPosition = new dVec2D(0,0);
        mGeometryColliders = new ArrayList<>();
        mDeathColliders = new ArrayList<>();
        fromXML(parser, context);
    }

    /// <summary>
    /// Creates a copy of the given Level Segment with an offset position
    /// </summary>
    /// <param name="base">the base LevelSegment of this new one</param>
    /// <param name="position">the OffsetPosition of the new Segment</param>
    public LevelSegment(LevelSegment base, dVec2D position){
        mGeometryID = base.mGeometryID;
        mPosition = position;
        mGeometryColliders = new ArrayList<>();
        mDeathColliders = new ArrayList<>();
        for(Collider collider : base.mGeometryColliders){
            mGeometryColliders.add(new Collider(collider, position));
        }
        for(Collider collider : base.mDeathColliders){
            mDeathColliders.add(new Collider(collider, position));
        }
        mSprite = base.mSprite;
    }

    /// <summary>
    /// Returns the Geometry ID of the Segment.
    /// </summary>
    /// <returns>the Geometry ID</returns>
    public int getGeometryID(){
        return mGeometryID;
    }

    /// <summary>
    /// Moves the LevelSegment by this Amount
    /// </summary>
    /// <param name"direction">the Direction Vector to move the Segment by</param>
    public void moveBy(dVec2D direction){
        for(Collider collider : mGeometryColliders)
            collider.moveBy(direction);
        for(Collider collider : mDeathColliders)
            collider.moveBy(direction);
        mPosition.add(direction);
    }


    /// <summary>
    /// Returns the Sprite of the LevelSegment
    /// </summary>
    /// <returns>the Sprite of the Segment</returns>
    public Sprite getSprite(){
        return mSprite;
    }

    /// <summary>
    /// Returns the minimal x and y coordinates of the Segment
    /// since Segments are always max height, y is always 0
    /// </summary>
    /// <returns>the upper left corner position</returns>
    public dVec2D upperLeftCoordinate(){
        final double minScreen = 0;
        return new dVec2D(mPosition.x, minScreen);
    }

    /// <summary>
    /// Returns the maximal x and y coordinates of the Segment
    /// since Segments are always max height, y is always the maximum screen coordinate
    /// </summary>
    /// <returns>the lower right corner position</returns>
    public dVec2D lowerRightCoordinate(){
        //TODO:set correct height here
        final double maxScreen = 1;
        return new dVec2D(mSprite.getWidth()+mPosition.x, maxScreen);
    }

    /// <summary>
    /// Checks for Collision with LevelGeometry. Returns the collided Collider or null if none.
    /// </summary>
    /// <param name="target">the Collider to check<param>
    /// <returns>null if no collider collides or the collider that collided</returns>
    public Collider checkGeometryCollision(Collider target){
        for(Collider collider : mGeometryColliders)
            if(target.checkCollision(collider)) return collider;
        return null;
    }

    /// <summary>
    /// returns the ArrayList of Geometry colliders
    /// </summary>
    /// <returns>the ArrayList of Geometry Colliders</returns>
    public ArrayList<Collider> getGeometryColliders(){
        return mGeometryColliders;
    }

    /// <summary>
    /// Returns the List of Death Colliders
    /// </summary>
    /// <returns>the List of Death Colliders</returns>
    public ArrayList<Collider> getDeathColliders(){
        return mDeathColliders;
    }

    /// <summary>
    /// Checks weather a Death Collider was hit.
    /// </summary>
    /// <param name="target">the Collider to check against<param>
    /// <returns>true when death collider was hit</returns>
    public boolean checkDeath(Collider target){
        for(Collider collider : mDeathColliders) if(target.checkCollision(collider)) return true;
        return false;
    }

    /// <summary>
    /// adds a Collider based on the given Polygon to the given List of Colliders
    /// </summary>
    /// <param name="polygon">the Polygon<param>
    /// <param name="list">the List of Colliders<param>
    private static void addPolygonToColliders(Polygon polygon, ArrayList<Collider> list){
        if (polygon.getVertices().size() == 4) { //maybe check if AABB can be used
            Polygon p = BoundingBox2D.canBeAABB(polygon);
            if (p != null) {
                list.add(new BoundingBox2D(
                        p.getVertices().get(0),
                        p.getVertices().get(2)
                ));
                return;
            }
        }
        list.add(new Collider(polygon));
    }

    /// <summary>
    /// Reads the LevelSegment from an XML Parser
    /// </summary>
    /// <param name="parser">the XML Parser to read from</param>
    /// <param name="context">the Context to read the Resource from</param>
    private void fromXML(XmlPullParser parser, Context context)
    throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "LevelSegment");
        while(parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            switch(parser.getName()){
                case "Sprite":
                    mSprite = Sprite.readSprite(parser, context);
                break;
                case "Collection":
                    String collisionType = parser.getAttributeValue(null, "collisionType");
                    if(collisionType == null) collisionType = "barrier";

                    for(Polygon polygon : Collider.readPolygons(parser))
                        switch(collisionType){
                            case "death":
                                addPolygonToColliders(polygon, mDeathColliders);
                            break;
                            case "barrier":
                            default:
                                addPolygonToColliders(polygon, mGeometryColliders);
                            break;
                        }
                break;
                case "LevelSocket":
                    LevelSocket socket = LevelSocket.XMLReadLevelSocket(parser);
                    switch(socket.mSide){
                        case Left:
                            leftSocket = socket;
                            break;
                        case Right:
                            rightSocket = socket;
                            break;
                    }
                    break;
                default:
//                    XMLNextStart(parser);
                    XMLSkip(parser);
                break;
            }
        }
        parser.require(XmlPullParser.END_TAG,null, "LevelSegment");
    }

    /// <summary>
    /// Draws the level segment to the screen
    /// </summary>
    /// <param name="canvas">the Canvas to draw to<param>
    public void draw(Canvas canvas){
        draw(canvas, false);
    }

    /// <summary>
    /// Draws the level segment to the screen
    /// </summary>
    /// <param name="canvas">the Canvas to draw to<param>
    /// <param name="wireframe">weather to draw Wireframes around the colliders or not<param>
    public void draw(Canvas canvas, boolean wireframe){
        mSprite.draw(canvas, (float)mPosition.x, (float)mPosition.y, null);

        Paint geometryPaint = new Paint();
        Paint deathPaint = new Paint();

        DashPathEffect effect = new DashPathEffect(new float[] {10f, 20f}, 0f);
        geometryPaint.setPathEffect(effect);
        geometryPaint.setARGB(255, 0, 255, 200);
        deathPaint.setPathEffect(effect);
        deathPaint.setARGB(255, 255, 100, 100);

        if(wireframe){
            for(Collider collider : mGeometryColliders)
                collider.draw(canvas, geometryPaint, geometryPaint);
            for(Collider collider : mDeathColliders)
                collider.draw(canvas,deathPaint, deathPaint);
        }
    }
}


/// <summary>
///
/// </summary>
/// <param name=""><param>
/// <returns></returns>
