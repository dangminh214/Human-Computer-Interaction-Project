package da.nzse.engine;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import da.nzse.spaceninja.R;

public class Level {
    private static LevelSegment groundSegment;

    //TODO: replace with actual value
    private final double screenWidth = 16000;

    private ArrayList<LevelSegment> mPossibleSegments;
    private Deque<LevelSegment> mSegments;
    private Map<Integer, ArrayList<LevelSegment>> mMatches;

    public Level(Context context){

        mPossibleSegments = new ArrayList<>();
        mSegments = new ArrayDeque<>();
        mMatches = new HashMap<>();

        XmlResourceParser parser = context.getResources().getXml(R.xml.ls_ground_segment);
        try {
            //read in ground segment, as it is special and where everything starts.
            while(parser.getEventType() != XmlPullParser.START_TAG)
                parser.next();
            groundSegment = new LevelSegment(parser,context);

            mPossibleSegments.add(groundSegment);

            parser = context.getResources().getXml(R.xml.level_segments);
            while(parser.getEventType() != XmlPullParser.START_TAG)
                parser.next();

            int eventType = parser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT){
                parser.require(XmlPullParser.START_TAG, null, "Collection");
                if(!parser.getAttributeValue(null, "type").equals("LevelSegment")) continue;
                while(parser.next() != XmlPullParser.END_TAG){
                    if (parser.getEventType() != XmlPullParser.START_TAG) continue;
                    mPossibleSegments.add(new LevelSegment(parser, context));
                }
                parser.require(XmlPullParser.END_TAG, null, "Collection");
                parser.next();
                eventType = parser.getEventType();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //precalculate list of matching Segments per other Segment
        for(LevelSegment checkSegment : mPossibleSegments){
            mMatches.put(checkSegment.getGeometryID(), new ArrayList<>());
            for(LevelSegment otherSegment : mPossibleSegments){
                if(checkSegment.rightSocket.matches(otherSegment.leftSocket))
                    mMatches.get(checkSegment.getGeometryID()).add(otherSegment);
            }
        }

        //insert first 2 segments as just ground
        double lastWidth = 0;
        for(int i = 1; i < 2; i++){
            LevelSegment currentSegment = new LevelSegment(groundSegment, new dVec2D(lastWidth,0));
            mSegments.addLast(currentSegment);
            lastWidth += currentSegment.getSprite().getWidth();
        }

        //generate rest of segments until a segment is completely offscreen.
        while (lastWidth < screenWidth) {
            LevelSegment newLevelSegment = getMatchingSegment();
            mSegments.addLast(new LevelSegment(newLevelSegment,
                    new dVec2D(new dVec2D(lastWidth, 0))));
            lastWidth += newLevelSegment.getSprite().getWidth();
        } ;

    }

    /// <summary>
    /// moves the entire Level to the Left to scroll
    /// </summary>
    /// <param name="amount">the Value to move the Level to the left by</param>
    public void moveLeftBy(double amount){
        for(LevelSegment segment : mSegments){
            segment.moveBy(new dVec2D(-amount, 0));
        }
        //check if first Segment is still visible. if not, generate a new Segment
        if(mSegments.getFirst().lowerRightCoordinate().x < 0)
            generateNewSegment();
    }

    /// <summary>
    /// generates a new Segment that fits to the end of the current Segment Queue
    /// </summary>
    /// <returns>a new Segment that has a matching Socket with the last Segment</returns>
    private LevelSegment getMatchingSegment(){
        //get a random matching segment
        ArrayList<LevelSegment> matches = mMatches.get(mSegments.getLast().getGeometryID());
        int index = (int)(Math.random() * matches.size());
        return matches.get(index);
    }

    /// <summary>
    /// generates a new Segment and ads it to the Right of the Screen,
    /// removing the first Segment on the left in the process
    /// </summary>
    private void generateNewSegment() {
        //get a random matching segment
        cycleSegment(getMatchingSegment());
    }

    /// <summary>
    /// cycles a new Segment into the Queue and removes the first Segment
    /// </summary>
    /// <param name="newSegment">the new segment to add to the end of the Queue</param>
    private void cycleSegment(LevelSegment newSegment) {
        //remove the first segment from the list
        mSegments.removeFirst();
        //add segment to the end of the list
        mSegments.addLast(new LevelSegment(newSegment,
                new dVec2D(mSegments.getLast().lowerRightCoordinate().x,0)));
    }

    /// <summary>
    /// Checks for Collision with the Environment and returns a resolution vector or null
    /// only checks the Segment that could potentially collide based on its start coordinates
    /// </summary>
    /// <param name="target">the target Entity<param>
    /// <returns>a resolution vector when collision occurred, null if no collision</returns>
    public dVec2D checkGeometryCollision(Entity target){
        LevelSegment checkSegment = getClosestSegment(target, new ArrayList<>(mSegments));
        for(LevelSegment segment : mSegments){
            if(segment.upperLeftCoordinate().x < target.getPosition().x &&
            segment.lowerRightCoordinate().x > target.getPosition().x){
                checkSegment = segment;
                break;
            }
        }
        if(checkSegment == null) return null;
        Collider checkCollider = checkSegment.checkGeometryCollision(target.getCollider());
        if(checkCollider == null) return null;
        return checkCollider.resolveCollision(target.getCollider());
    }

    /// <summary>
    /// Returns the LevelSegment best containing the target Entity
    /// </summary>
    /// <param name="target">the target entity</param>
    /// <param name="target">the List of Segments to check</param>
    /// <returns>the best suited LevelSegment</returns>
    public static LevelSegment getClosestSegment(Entity target, List<LevelSegment> segments){
        for(LevelSegment segment : segments){
            if( segment.upperLeftCoordinate().x < target.getPosition().x &&
                segment.lowerRightCoordinate().x > target.getPosition().x) {
                return segment;
            }
        }
        return null;
    }

    /// <summary>
    /// Returns the LevelSegment best containing the target Entity
    /// </summary>
    /// <param name="target">the target entity</param>
    /// <returns>the best suited LevelSegment</returns>
    public LevelSegment getClosestSegment(Entity target){
        return getClosestSegment(target,new ArrayList<>(mSegments));
    }

    /// <summary>
    /// Checks for Collision with Death Zones and returns true when Entity should die
    /// </summary>
    /// <param name="target">the target Entity<param>
    /// <returns>true when the entity should die</returns>
    public boolean checkDeathCollision(Entity target){
        LevelSegment checkSegment = null;
        for(LevelSegment segment : mSegments){
            if(segment.upperLeftCoordinate().x < target.getPosition().x &&
                    segment.lowerRightCoordinate().x > target.getPosition().x){
                checkSegment = segment;
                break;
            }
        }
        if(checkSegment == null) return false;
        return checkSegment.checkDeath(target.getCollider());
    }

    /// <summary>
    /// Draws all the Segments in the Level
    /// </summary>
    /// <param name="canvas">the Canvas to draw to<param>
    public void draw(Canvas canvas){
        for(LevelSegment segment : mSegments)
            segment.draw(canvas);
    }

    /// <summary>
    /// Draws all the Segments in the Level
    /// </summary>
    /// <param name="canvas">the Canvas to draw to<param>
    public void draw(Canvas canvas, boolean wireframe){
        for(LevelSegment segment : mSegments){
            segment.draw(canvas, wireframe);
        }
    }

}
