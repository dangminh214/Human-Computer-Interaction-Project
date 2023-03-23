package da.nzse.engine;

import static da.nzse.engine.XMLCommon.XMLSkip;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/// <summary>
/// Represents a Polygon
/// </summary>
public class Polygon {

    private ArrayList<dVec2D> mVertices;
    private ArrayList<dVec2D> mNormals;
    private boolean mIsConvex;
    private dVec2D mCenter;

    /// <summary>
    /// Creates a Polygon from a list of Vertices
    /// </summary>
    /// <param name="vertices">the List of Vertices</summary>
    public Polygon(ArrayList<dVec2D> vertices){
        mVertices = vertices;

        mNormals = new ArrayList<>();
        for(int i=0; i < mVertices.size(); i++){
            mNormals.add(mVertices.get((i+1)%mVertices.size())
                    .getDifference(mVertices.get(i)).getNormal());
        }

        mIsConvex = isConvex(this);
        mCenter = findCenter(this);
    }

    /// <summary>
    /// returns weather the Polygon is Convex or not
    /// </summary>
    /// <returns>true when polygon is convex</returns>
    public boolean isConvex(){
        return mIsConvex;
    }

    /// <summary>
    /// returns the Normals to all the Edges of the Polygon
    /// </summary>
    /// <returns>a List of all the Normal Vectors to the Edges of the Polygon</returns>
    public ArrayList<dVec2D> getNormals(){
        return mNormals;
    }

    /// <summary>
    /// returns all the Vertices of the Polygon
    /// </summary>
    /// <returns>a List of all the Vertices of the Polygon</returns>
    public ArrayList<dVec2D> getVertices() {
        return mVertices;
    }

    /// <summary>
    /// returns the Center of the Polygon
    /// </summary>
    /// <returns>the Center of the Polygon</returns>
    public dVec2D getCenter(){
        return mCenter;
    }

    /// <summary>
    /// Moves the Polygon in the given Direction
    /// </summary>
    /// <param name="direction">the Direction to move the Polygon in</param>
    public void moveBy(dVec2D direction){

        for(int i = 0; i < mVertices.size(); i++)
            mVertices.set(i, mVertices.get(i).getSum(direction));

        //when shifting all the Vertices, the Center also moves in the same direction.
        mCenter = mCenter.getSum(direction);
        //normals don't have to be moved because they stay the same,
        //just start from a different position
    }

    /*
    /// <summary>
    /// returns the Area of the Polygon
    /// </summary>
    /// <param name="p">the Polygon in Question</param>
    /// <returns>the Area of the Polygon</returns>
    static private double getArea(Polygon p){
        double area = 0;
        for(int index=0; index < p.mVertices.size()-1; index++){
            dVec2D current = p.mVertices.get(index);
            dVec2D next = p.mVertices.get(index+1);
            area += current.x * next.y - next.x * current.y;
        }
        return area*.5;
    }
     */

    /// <summary>
    /// returns the center of the Polygon
    /// </summary>
    /// <param name="p">the Polygon in Question</param>
    /// <returns>the Center of the Polygon</returns>
    static private dVec2D findCenter(Polygon p){
        double area = 0;
        double x=0, y=0;
        for(int index = 0; index < p.mVertices.size(); index++){
            dVec2D current = p.mVertices.get(index);
            dVec2D next = p.mVertices.get((index+1) % p.mVertices.size());
            double currentArea = (current.x * next.y) - (next.x * current.y);
            area += currentArea;
            x += (current.x+next.x) * currentArea;
            y += (current.y+next.y) * currentArea;
        }
        area /= 2.0;
        x /= 6.0*area;
        y /= 6.0*area;

        return new dVec2D(x,y);
    }

    /// <summary>
    /// returns the number of Edges in the Polygon
    /// </summary>
    /// <param name="p">the Polygon in Question</param>
    /// <returns> the number of Edges in the Polygon</returns>
    static private int getEdgeCount(Polygon p){
        return p.mVertices.size()-1;
    }

    /// <summary>
    /// Returns the index-th edge of the polygon
    /// </summary>
    /// <param name="p">the Polygon in Question</param>
    /// <param name="index">the index of the edge</param>
    /// <returns>the Vector representing the Edge</returns>
    static private dVec2D getEdge(Polygon p, int index){
        dVec2D edge = new dVec2D(p.mVertices.get((index+1)%p.mVertices.size()));
        edge.getDifference(p.mVertices.get(index%p.mVertices.size()));
        return edge;
    }

    /// <summary>
    /// checks weather the polygon is convex
    /// </summary>
    /// <param name="p">the Polygon to check</param>
    /// <returns>true when Polygon is convex</returns>
    static private boolean isConvex(Polygon p){

        for(int edgeIndex=0; edgeIndex < getEdgeCount(p); edgeIndex++){
            if(getEdge(p,edgeIndex).angle(getEdge(p,edgeIndex+1), false) > 180)
                return false;
        }

        return true;
    }

    /// <summary>
    /// Checks weather the given Point is inside or outside the given Polygon
    /// </summary>
    /// <param name="point">the Point to check</param>
    /// <param name="polygon">the Polygon to check against</param>
    /// <returns>True when Point lies inside Polygon</return>
    static boolean insidePolygon(dVec2D point, Polygon polygon){
        Boolean sign = null;
        for(dVec2D vertex : polygon.mVertices){
            if(sign==null){
                sign = (point.cross(vertex) > 0);
                continue;
            }
            if((point.cross(vertex) > 0) != sign) return false;
        }
        return true;
    }

    /// <summary>
    /// Returns a new Polygon based on the intersection of the 2 given Polygons
    /// </summary>
    /// <param name="p1">the First Polygon</param>
    /// <param name="p2">the second Polygon</param>
    /// <returns>
    //  the Polygon representing the Overlapped area between the other 2 polygons or
    /// null when the polygons don't overlap.
    /// VERY INEFFICIENT FOR CHECKING COLLISION!!
    /// </returns>
    static Polygon overlap(Polygon p1, Polygon P2){

        return null;
    }

    /// <summary>
    /// Reads in a Polygon from inside a Polygon Collection
    /// <summary>
    /// <param name="parser">the XML Parser to read from</param>
    /// <returns>a Polygon read from the XML Parser</returns>
    public static Polygon XMLReadPolygon(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        ArrayList<dVec2D> vertices = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, "Polygon");
        while(parser.next() != XmlPullParser.END_TAG){
            if(!parser.getName().equals("vertex")){
                XMLSkip(parser);
                continue;
            }
            try {
                dVec2D vertex = new dVec2D(
                        Double.parseDouble(parser.getAttributeValue(null, "x")),
                        Double.parseDouble(parser.getAttributeValue(null, "y"))
                );
                vertices.add(vertex);
            } catch(Exception E) {
                E.printStackTrace();
            }
            parser.next();
            parser.require(XmlPullParser.END_TAG, null, "vertex");
        }
        parser.require(XmlPullParser.END_TAG, null, "Polygon");
        return new Polygon(vertices);
    }


}
