package da.nzse.engine;

import androidx.annotation.NonNull;

public class dVec2D {
    public double x, y;

    dVec2D(){x = 0; y = 0;}
    dVec2D(double _x, double _y) {x=_x; y=_y;}
    dVec2D(dVec2D other){x=other.x; y=other.y;}

    dVec2D getSum(dVec2D other){ return new dVec2D(x+other.x, y+other.y); }
    dVec2D getDifference(dVec2D other){ return new dVec2D(x-other.x, y-other.y); }

    void add(dVec2D other) { x += other.x; y += other.y; }
    void sub(dVec2D other) { x -= other.x; y -= other.y; }

    public void scale(double scalar) {
        x *= scalar;
        y *= scalar;
    }

    public dVec2D getScaled(double scalar) {
        return new dVec2D(x*scalar, y*scalar);
    }

    public double abs(){
        return Math.sqrt(x*x+y*y);
    }

    public void normalize(){
        double value = abs();
        x /= value;
        y /= value;
    }

    public dVec2D getNormalized(){
        double value = abs();
        return new dVec2D((double)x/value, (double)y/value);
    }

    public double dot(dVec2D other) {
        return x*other.x + y*other.y;
    }

    public double angle(dVec2D other, boolean isNormalized) {
        double angle = 0;
        if(isNormalized) {
            angle = Math.toDegrees(Math.atan2(other.y, other.x) - Math.atan2(y,x));
        } else {
            dVec2D thisNormalized = getNormalized();
            dVec2D otherNormalized = other.getNormalized();
            angle = Math.toDegrees(
                    Math.atan2(otherNormalized.y, otherNormalized.x) -
                    Math.atan2(thisNormalized.y, thisNormalized.x)
            );
        }
        if(angle<0) angle+= 360;
        return angle;
    }

    public void rotate(double angle){
        double theta = Math.toRadians(angle);
        double cs = Math.cos(theta);
        double sn = Math.sin(theta);
        double x = this.x * cs - this.y * sn;
        double y = this.x * sn + this.y * cs;
        this.x = x;
        this.y = y;
    }

    /// <summary>
    /// Calculates the Projection Vector onto another Vector
    /// </summary>
    /// <param name="other">the Vector to project onto</param>
    /// <returns>the projected Vector onto other</returns>
    public dVec2D project(dVec2D other){
        return other.getScaled(this.dot(other)/(other.abs()*other.abs()));
    }

    /// <summary>
    /// Calculate the Cross Product into 3 Dimensions by simulating a 3 in the 3rd Coordinate of the
    /// Vectors. This can be used for Polygon Calculations
    /// </summary>
    /// <param name="other">the other vector to check against</param>
    /// <returns>a Scalar representing the Cross Product</returns>
    public double cross(dVec2D other) {
        return (x*other.x)-(y*other.y);
    }

    public dVec2D getNormal(){
        return new dVec2D(-1*y,x);
    }

    @NonNull
    @Override public String toString(){ return "dVec2D("+x+","+y+")"; }
}
