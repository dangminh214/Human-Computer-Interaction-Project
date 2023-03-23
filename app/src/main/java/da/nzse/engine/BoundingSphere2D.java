package da.nzse.engine;

import java.util.ArrayList;

public class BoundingSphere2D extends Collider {

    /// <summary>
    /// The number of Vertices to use for a Circle when converted to Vertices
    /// </summary>
    private static final int cCircleAccuracy = 16;

    /// <summary>
    /// Creates the necessary Vertices for a Circle as convex Polygon
    /// </summary>
    /// <param name="radius">the Radius of the Circle</param>
    /// <param name="center">the Center of the Circle</param>
    /// <returns>an Array List of Vertices that represent the Circle</param>
    private static ArrayList<dVec2D> createVertices(double radius, dVec2D center){
        ArrayList<dVec2D> vertices = new ArrayList();

        for(int n = 0; n < cCircleAccuracy+1; n++ ){
            double factor = (2*n*Math.PI)/(cCircleAccuracy+1);
            dVec2D vertex = new dVec2D(Math.cos(factor),Math.sin(factor))
                    .getScaled(radius)
                    .getSum(center);
            vertices.add(vertex);
        }
        return vertices;
    }

    private double mRadius;
    private dVec2D mPosition;

    BoundingSphere2D(double radius, dVec2D position){
        super(createVertices(radius, position));
        mRadius = radius;
        mPosition = position;
    }

    BoundingSphere2D(double radius, int x, int y) {
        super(createVertices(radius, new dVec2D(x,y)));
        mRadius = radius;
        mPosition = new dVec2D(x,y);
    }

    public boolean checkCollision(BoundingSphere2D other) {
        return Math.abs(mPosition.getDifference(other.mPosition).abs()) >= mRadius;
    }

}
