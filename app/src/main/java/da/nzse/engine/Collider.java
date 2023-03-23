package da.nzse.engine;

import static da.nzse.engine.XMLCommon.XMLSkip;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/// <summary>
/// Generic Collider for Convex Polygons
/// !MAKE SURE THE POLYGON IS CONVEX!
/// </summary>
public class Collider {
    protected Polygon mPolygon;

    /// <summary>
    /// Creates a Collider and initializes its Normals
    /// </summary>
    /// <param name="vertices">the Vertices of the convex Polygon to use as shape</param>
    public Collider(ArrayList<dVec2D> vertices){
        mPolygon = new Polygon(vertices);
    }

    /// <summary>
    /// Creates a Collider from a Polygon
    /// </summary>
    /// <param name="polygon">the Polygon for the Collider</param>
    public Collider(Polygon polygon){
        mPolygon = polygon;
    }

    /// <summary>
    /// Creates a new Collider from a Base Collider with an offset
    /// </summary>
    /// <param name="base">the Collider to base this one off</param>
    /// <param name="offset">the Offset of this Collider from the base Collider</param>
    public Collider(Collider base, dVec2D offset){
        ArrayList<dVec2D> vertices = new ArrayList<>();
        for(dVec2D vertex: base.mPolygon.getVertices()){
            vertices.add(vertex.getSum(offset));
        }
        mPolygon = new Polygon(vertices);
    }

    /// <summary>
    /// Moves the Collider in the given Direction
    /// </summary>
    /// <param name="direction">the Direction Vector to move the Collider by</param>
    public void moveBy(dVec2D direction){
        mPolygon.moveBy(direction);
    }

    /// <summary>
    /// returns the Center of the Colliders internal Polygon
    /// </summary>
    /// <returns>the Center of the internal Polygon</returns>
    public dVec2D getCenter(){
        return mPolygon.getCenter();
    }

    /// <summary>
    /// Checks for a set of Normals weather the projections of the given
    /// Vertices overlap.
    /// </summary>
    /// <param name="normals">the List of Normals to check the Vertex Projections for</param>
    /// <param name="verticesA">the first set of Vertices</param>
    /// <param name="verticesB">the second List of Vertices</param>
    /// <returns>false when no overlap was found
    private static boolean SATCheckNormals(ArrayList<dVec2D> normals,
                                           ArrayList<dVec2D> verticesA,
                                           ArrayList<dVec2D> verticesB){
        for(dVec2D normal : normals){
            Double aMax=null, aMin=null, bMax=null, bMin=null;
            for(dVec2D vertex : verticesA){
                double dot = vertex.dot(normal);
                if(aMax == null || aMax < dot)
                    aMax = dot;
                if(aMin == null || aMin > dot)
                    aMin = dot;
            }

            for(dVec2D vertex : verticesB){
                double dot = vertex.dot(normal);
                if(bMax == null || bMax < dot)
                    bMax = dot;
                if(bMin == null || bMin > dot)
                    bMin = dot;
            }

            if(aMin > bMax || aMax < bMin)
                return false;
        }
        return true;
    }

    /// <summary>
    /// Checks weather 2 Colliders collide.
    /// </summary>
    /// <param name="other">the other Collider</param>
    /// <returns>true when collision occurred</return>
    public boolean checkCollision(Collider other) {
        if(other == null) return false;
        if(!SATCheckNormals(mPolygon.getNormals(),
                         mPolygon.getVertices(),
                         other.mPolygon.getVertices()))
            return false;
        return SATCheckNormals(other.mPolygon.getNormals(),
                            other.mPolygon.getVertices(),
                            mPolygon.getVertices());
    }

    /// <summary>
    /// statically resolves Collision (by returning a Vector that would resolve it).
    /// This version is using a modified SAT
    /// the resolution vector will always resolve the collision by moving this collider.
    /// to move the object that was collided with, invert the vector.
    /// </summary>
    /// <param name="other">the Collider to resolve Collision against</param>
    /// <returns>a Vector to resolve the Collision by moving the current collider</returns>
    public dVec2D resolveCollision(Collider other) {

        dVec2D resolution = null;
        double depth = Double.MAX_VALUE;

        for(dVec2D normal : mPolygon.getNormals()){
            Double minA=null, maxA=null, minB=null, maxB=null;
            for(dVec2D vertex : mPolygon.getVertices()){
                double value = vertex.dot(normal);
                if(minA == null || minA > value) minA=value;
                if(maxA == null || maxA < value) maxA=value;
            }
            for(dVec2D vertex : other.mPolygon.getVertices()){
                double value = vertex.dot(normal);
                if(minB == null || minB > value) minB=value;
                if(maxB == null || maxB < value) maxB=value;
            }
            if(minA >= maxB || maxA <= minB) return null;
            if(maxB - minA < maxA - minB){
                if(maxB-minA < depth){
                    depth = maxB-minA;
                    depth *= -1;
                    resolution = normal;
                }
            } else {
                if(maxA-minB < depth){
                    depth = maxA-minB;
                    resolution = normal;
                }
            }
        }

        for(dVec2D normal : other.mPolygon.getNormals()){
            Double minA=null, maxA=null, minB=null, maxB=null;
             for(dVec2D vertex : mPolygon.getVertices()){
                double value = vertex.dot(normal);
                if(minA == null || minA > value) minA=value;
                if(maxA == null || maxA < value) maxA=value;
            }
            for(dVec2D vertex : other.mPolygon.getVertices()){
                double value = vertex.dot(normal);
                if(minB == null || minB > value) minB=value;
                if(maxB == null || maxB < value) maxB=value;
            }
            if(minA >= maxB || maxA <= minB) return null;

            if(maxB - minA < maxA - minB){
                if(maxB-minA < depth){
                    depth = maxB-minA;
                    depth *= -1;
                    resolution = normal;
                }
            } else {
                if(maxA-minB < depth){
                    depth = maxA-minB;
                    resolution = normal;
                }
            }
        }

        depth /= resolution.abs();
        resolution = resolution.getNormalized();
        resolution.scale(depth);
        return resolution;
    }

    /*
    /// <summary>
    /// Returns the Intersection Point between the 2 Lines
    /// </summary>
    /// <param name="line1Start">the Starting Point of line 1<param>
    /// <param name="line1End">the End Point of line 1<param>
    /// <param name="line2Start">the Start Point of line 2<param>
    /// <param name="line2End">the End Point of line 2<param>
    /// <returns>the Intersection Point if it exists, null if no intersection</returns>
    private static dVec2D lineIntersection(dVec2D line1Start, dVec2D line1End,
                                           dVec2D line2Start, dVec2D line2End){
        dVec2D edge1 = line1End.getDifferende(line1Start);
        dVec2D edge2 = line2End.getDifferende(line2Start);

        //check for parallel lines. those cannot intersect or completely intersect.
        //we cannot handle complete intersection for collision resolution..
        if(edge1.dot(edge2) == 1.0) return null;

        //use determinant method for line intersection
        double  x11 = line1Start.x, x12 = line1End.x, y11 = line1Start.y, y12 = line1End.y,
                x21 = line2Start.x, x22 = line2End.x, y21 = line2Start.y, y22 = line2End.y;

        double f1 = (x11*y12) - (y11*x12);
        double f2 = (x21*y22) - (y21*x22);
        double f3 = ( (x11 - x12) * (y21 - y22) ) - ( (y11 - y12) * (x21 - x22) );

        if(f3 == 0) return null;

        double  x = ((f1*(x21 - x22)) - ((x11 - x22)*f2)) / f3,
                y = ((f1*(y21 - y22)) - ((y11 - y22)*f2)) / f3;

        return new dVec2D(x,y);
    }
*/

    /// <summary>
    ///
    /// </summary>
    /// <param name=""><param>
    /// <returns></returns>

/*
    /// <summary>
    /// statically resolves Collision (by returning a Vector that would resolve it)
    /// the resolution vector will always resolve the collision by moving this collider.
    /// to move the object that was collided with, invert the vector.
    /// </summary>
    /// <param name="other">the Collider to resolve Collision against</param>
    /// <returns>a Vector to resolve the Collision by moving the current collider</returns>
    /// TODO: Fix Bugs in Collision Resolution.
    public dVec2D resolveCollision(Collider other) {

        dVec2D displacement = new dVec2D(0,0);
        for(int p = 0; p < mPolygon.getVertices().size(); p++) {

            dVec2D line_r1s = mPolygon.getCenter();
            dVec2D line_r1e = mPolygon.getVertices().get(p);

            for(int q = 0; q < other.mPolygon.getVertices().size(); q++){
                dVec2D line_r2s = other.mPolygon.getVertices().get(q);
                dVec2D line_r2e = other.mPolygon.getVertices().get(
                        (q+1) % other.mPolygon.getVertices().size());

                double h = (line_r2e.x - line_r2s.x) * (line_r1s.y - line_r1e.y) -
                           (line_r1s.x - line_r1e.x) * (line_r2e.y - line_r2s.y);
                double t1 = ((line_r2s.y - line_r2e.y) * (line_r1s.x - line_r2s.x) +
                            (line_r2e.x - line_r2s.x) * (line_r1s.y - line_r2s.y)) / h;
                double t2 = ((line_r1s.y - line_r1e.y) * (line_r1s.x - line_r2s.x) +
                            (line_r1e.x - line_r1s.x) * (line_r1s.y - line_r2s.y)) / h;

                if (t1 >= 0.0f && t1 < 1.0f && t2 >= 0.0f && t2 < 1.0f) {
                    displacement.x -= (1.0f - t1) * (line_r1e.x - line_r1s.x);
                    displacement.y -= (1.0f - t1) * (line_r1e.y - line_r1s.y);
                }
            }
        }

        for(int p = 0; p < other.mPolygon.getVertices().size(); p++) {

            dVec2D line_r1s = other.mPolygon.getCenter();
            dVec2D line_r1e = other.mPolygon.getVertices().get(p);

            for(int q = 0; q < mPolygon.getVertices().size(); q++){
                dVec2D line_r2s = mPolygon.getVertices().get(q);
                dVec2D line_r2e = mPolygon.getVertices().get(
                        (q+1) % mPolygon.getVertices().size());

                double h = (line_r2e.x - line_r2s.x) * (line_r1s.y - line_r1e.y) -
                        (line_r1s.x - line_r1e.x) * (line_r2e.y - line_r2s.y);
                double t1 = ((line_r2s.y - line_r2e.y) * (line_r1s.x - line_r2s.x) +
                        (line_r2e.x - line_r2s.x) * (line_r1s.y - line_r2s.y)) / h;
                double t2 = ((line_r1s.y - line_r1e.y) * (line_r1s.x - line_r2s.x) +
                        (line_r1e.x - line_r1s.x) * (line_r1s.y - line_r2s.y)) / h;

                if (t1 >= 0.0f && t1 < 1.0f && t2 >= 0.0f && t2 < 1.0f) {
                    displacement.x -= (1.0f - t1) * (line_r1e.x - line_r1s.x);
                    displacement.y -= (1.0f - t1) * (line_r1e.y - line_r1s.y);
                }
            }
        }

        return displacement;
    }
*/
    /// <summary>
    /// Draw the Collider to the Canvas using the given Paints for their respective
    /// Elements of the Polygons and Colliders
    /// </summary>
    /// <param name="canvas">the Canvas to draw to</param>
    /// <param name="other">the Collider to check Collision against</param>
    /// <param name="vertexPaint">the Paint Object for Vertices when no collision occurred</param>
    /// <param name="edgePaint">the Paint Object for Edges when no collision occurred</param>
    /// <param name="centerPaint">the Paint Object for center Vertex</param>
    /// <param name="vertexHitPaint">the Paint Object for Vertices when collision occurred</param>
    /// <param name="edgeHitPaint">the Paint Object for Edges when collision occurred</param>
    /// <param name="resolvePaint">the Paint Object for the Resolve Vector</param>
    public void draw(Canvas canvas, @Nullable Collider other,
                     Paint vertexPaint, Paint edgePaint, @Nullable Paint centerPaint,
                     @Nullable Paint vertexHitPaint, @Nullable Paint edgeHitPaint,
                     @Nullable Paint resolvePaint) {

        Paint vertexDrawPaint = vertexPaint;
        Paint edgeDrawPaint = edgePaint;

        if(other != null){
            boolean collision = false;
            if(resolvePaint != null){
                dVec2D resolution = resolveCollision(other);
                if(resolution != null){
//                if(resolution.abs() > 0){
                    dVec2D center = mPolygon.getCenter();
                    dVec2D resVector = center.getSum(resolution);
                    collision = true;
                    Paint resolutionPaint = new Paint();
                    canvas.drawLine(
                            (float)center.x, (float)center.y,
                            (float)resVector.x, (float)resVector.y,
                            resolutionPaint);
                }
            } else {
                collision = checkCollision(other);
            }

            if(collision){
                vertexDrawPaint = vertexHitPaint;
                edgeDrawPaint = edgeHitPaint;
            }
        }

        dVec2D previousVertex = mPolygon.getVertices().get(mPolygon.getVertices().size()-1);
        for(dVec2D vertex : mPolygon.getVertices()){
            canvas.drawCircle((float)vertex.x, (float)vertex.y, 3, vertexDrawPaint);
            canvas.drawLine(
                    (float)vertex.x, (float)vertex.y,
                    (float)previousVertex.x, (float)previousVertex.y,
                    edgeDrawPaint);
            previousVertex = vertex;
        }
        canvas.drawCircle((float)getCenter().x,(float)getCenter().y,3,centerPaint);
    }

    /// <summary>
    /// Draw the Collider to the Canvas based on Collision with the other Canvas
    /// and optionally draw the resolution vector
    /// </summary>
    /// <param name="canvas">the Canvas to draw to</param>
    /// <param name="collider">the Collider to check collision against</param>
    /// <param name="resolve">weather or not to draw the Resolution Vector</param>
    public void draw(Canvas canvas, Collider collider, boolean resolve) {

        Paint vertexPaint = new Paint();
        Paint edgePaint = new Paint();
        Paint centerPaint = new Paint();
        Paint vertexHitPaint = new Paint();
        Paint edgeHitPaint = new Paint();
        Paint vertexResolvePaint = null;
        if (resolve){
            vertexResolvePaint = new Paint();
            vertexResolvePaint.setARGB(255,20,20,255);
            vertexResolvePaint.setStrokeCap(Paint.Cap.ROUND);
        }

        vertexPaint.setARGB(255,50,200,50);
        edgePaint.setARGB(255,0,255,0);
        centerPaint.setARGB(255,0,255,255);
        vertexHitPaint.setARGB(255,200,50,50);
        edgeHitPaint.setARGB(255,255,0,0);

        draw(canvas, collider,
            vertexPaint, edgePaint, centerPaint,
            vertexHitPaint, edgeHitPaint,
            vertexResolvePaint);

    }

    /// <summary>
    /// Draws the Collider to the Canvas and changes Color when Collision occurred
    /// </summary>
    /// <param name="canvas">the Canvas to draw to<param>
    /// <param name="collider">the Collider to check Collision with<param>
    public void draw(Canvas canvas, Collider collider) {
        draw(canvas, collider, false);
    }

    /// <summary>
    /// Draw the Collider to the Canvas
    /// </summary>
    /// <param name="canvas">the Canvas to draw to</param>
    public void draw(Canvas canvas){
        Paint vertexPaint = new Paint();
        Paint edgePaint = new Paint();
        Paint centerPaint = new Paint();

        centerPaint.setARGB(0,255,255,0);
        vertexPaint.setARGB(255,50,200,50);
        edgePaint.setARGB(255,0,255,0);

        draw(canvas,null, vertexPaint, edgePaint, centerPaint, null,null,null);
    }

    /// <summary>
    /// Draws the Collider to the Canvas, using the given Colors for edges and vertices
    /// </summary>
    /// <param name="canvas">the Canvas to draw to<param>
    /// <param name="edgeColor">the Pain Object that will be used to draw the Edges<param>
    /// <param name="vertexColor">the Paint Object that will be used to draw the Vertices<param>
    public void draw(Canvas canvas, Paint edgeColor, Paint vertexColor){
        Paint centerPaint = new Paint();
        centerPaint.setARGB(0,255,255,0);
        draw(canvas, null, vertexColor, edgeColor, centerPaint, null, null, null);
    }

    /// <summary>
    /// Reads in a Collection of Polygons from the XML Parser
    /// <summary>
    /// <param name="parser">the XML Parser to read from</param>
    /// <returns>a List of Polygons read from a collection tag in the xl file</returns>
    public static ArrayList<Polygon> readPolygons(XmlPullParser parser)
    throws IOException, XmlPullParserException {
        ArrayList<Polygon> polygons = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, "Collection");
        if(!parser.getAttributeValue(null, "type").equals("Polygon")) return polygons;
        parser.next();
        do {
            String name = parser.getName();
            if(!name.equals("Polygon")) XMLSkip(parser);
            else {
                polygons.add(Polygon.XMLReadPolygon(parser));
            }
        } while(parser.next() != XmlPullParser.END_TAG);
        parser.require(XmlPullParser.END_TAG, null, "Collection");
        return polygons;
    }
}
