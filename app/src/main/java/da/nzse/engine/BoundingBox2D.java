package da.nzse.engine;

import java.util.ArrayList;
import java.util.Arrays;

public class BoundingBox2D extends Collider {

    private dVec2D mCornerTL;
    private dVec2D mCornerBR;

    BoundingBox2D(int top, int left, int bottom, int right){
        super(new Polygon(
                new ArrayList<>(Arrays.asList(
                new dVec2D(left,top),
                new dVec2D(right,top),
                new dVec2D(right,bottom),
                new dVec2D(left,bottom)
        ))));
        mCornerTL = new dVec2D(left, top);
        mCornerBR = new dVec2D(right,bottom);
    }

    BoundingBox2D(dVec2D tl, dVec2D br){
        super(new Polygon(
                new ArrayList<>(Arrays.asList(
                new dVec2D(tl.x,tl.y),
                new dVec2D(br.x,tl.y),
                new dVec2D(br.x,br.y),
                new dVec2D(tl.x,br.y)
        ))));
        mCornerTL = tl;
        mCornerBR = br;
    }

    /// <summary>
    /// Checks collision with other AABB. This is way faster
    /// and more efficient than using the general Polygon approach
    /// </summary>
    /// <param name="other">the other Bounding Box</param>
    /// <returns>true when collision occurs</returns>
    public boolean checkCollision(BoundingBox2D other) {
        return (mCornerBR.x < other.mCornerTL.x ||
                mCornerTL.x > other.mCornerBR.x ||
                mCornerTL.y > other.mCornerBR.y ||
                mCornerBR.y < other.mCornerTL.y );
    }

    /// <summary>
    /// Checks weather a given Polygon can be a Axis Aligned Bounding Box (or BoundingBox2D)
    /// if so, make sure that the vertices are in the correct orientation
    /// </summary>
    /// <param name="polygon">the Polygon to check and/or modify</param>
    /// <returns>a new Polygon when it can be used, null when not</returns>
    public static Polygon canBeAABB(Polygon polygon){
        if(polygon.getVertices().size() != 4) return null;
        //a non convex polygon with 4 vertices is hourglass shaped -> not AABB material
        if(!polygon.isConvex()) return null;

        ArrayList<dVec2D> vertices = polygon.getVertices();

        final dVec2D unitX = new dVec2D(1,0);
        final dVec2D unitY = new dVec2D(0,1);

        Double top = null, left = null, bottom = null, right = null;

        //if any edge is not perpendicular to either axis it is no AABB
        for(int i=0; i < vertices.size(); i++){
            dVec2D vertex = vertices.get(i);
            dVec2D edge = vertices.get((i+1)%vertices.size()).getDifference(vertex);
            edge.normalize();
            if(edge.dot(unitX) != 0.0 && edge.dot(unitY) != 0.0) return null;
            //while looping the corners may already be calculated to save a loop in the next step
            if(top      == null || top      > vertex.y) top     = vertex.y;
            if(bottom   == null || bottom   < vertex.y) bottom  = vertex.y;
            if(left     == null || left     > vertex.x) left    = vertex.x;
            if(right    == null || right    < vertex.x) right   = vertex.x;
        }

        // at this point we know its axis aligned and square, but still have to check
        // and maybe transform it to have the upper left corner and lower right corner in the
        // correct places. luckily we already calculated the coordinates.
        vertices = new ArrayList<>();
        vertices.add(new dVec2D(top, left));
        vertices.add(new dVec2D(top,right));
        vertices.add(new dVec2D(bottom,right));
        vertices.add(new dVec2D(bottom,left));

        return new Polygon(vertices);
    }

}
