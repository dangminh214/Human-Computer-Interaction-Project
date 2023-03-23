package da.nzse.engine;

import android.graphics.Canvas;
import android.graphics.Matrix;

public interface Drawable {
    /// <summary>
    /// Draw the Drawable to the Canvas at Position x,y after applying the Transform
    /// </summary>
    /// <param name="canvas">the Canvas to draw to</param>
    /// <param name="x">the X Coordinate where to Draw</param>
    /// <param name="y">the Y Coordinate where to Draw</param>
    /// <param name="transform">the Transformation to apply</param>
    public void draw(Canvas canvas, float x, float y, Matrix transform);
}
