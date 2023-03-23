package da.nzse.engine;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/// <summary>
/// Represents an Entity with a physics system and sprite
/// </summary>
public abstract class Entity {
    private dVec2D mPosition;
    private dVec2D mVelocity = new dVec2D(0,0);
    public ArrayList<dVec2D> mForces;
    private double maxAbsoluteVelocity = Double.POSITIVE_INFINITY;
    private Collider mCollider;

    private Sprite mSprite;
    public Matrix mTransform;

    private int mScreenDimensions[] = {0,0};

    /// <summary>
    /// Creates the Entity with the given Sprite
    /// </summary>
    /// <param name="sprite">the Sprite to assign to the entity</param>
    public Entity(Sprite sprite, @Nullable Collider collider) {
        mSprite = sprite;
        mScreenDimensions[0] = Resources.getSystem().getDisplayMetrics().widthPixels;
        mScreenDimensions[1] = Resources.getSystem().getDisplayMetrics().heightPixels;
        mTransform = new Matrix();
        mForces = new ArrayList<dVec2D>();
        mCollider = collider;
    }

    /// <summary>
    /// Creates the Entity without Sprite
    /// </summary>
    public Entity() {
        mSprite = null;
        mScreenDimensions[0] = Resources.getSystem().getDisplayMetrics().widthPixels;
        mScreenDimensions[1] = Resources.getSystem().getDisplayMetrics().heightPixels;
        mTransform = new Matrix();
        mForces = new ArrayList<dVec2D>();
        mCollider = null;
    }

    /// <summary>
    /// Draws the Sprite of the Entity to the given Canvas
    /// </summary>
    /// <param name="canvas">the Canvas to draw the Sprite to</param>
    public void draw(Canvas canvas) {
        Paint pointPaint = new Paint();
        pointPaint.setARGB(255,0,255,255);
        canvas.drawCircle((float)mPosition.x,(float)mPosition.y,5,pointPaint);
        mSprite.draw(canvas, (float)mPosition.x, (float)mPosition.y, mTransform);
    }

    /// <summary>
    /// sets the Sprite of the Entity to a new one
    /// </summary>
    /// <param name="sprite">the new Sprite for the Entity</sprite>
    public void setSprite(Sprite sprite){
        mSprite = sprite;
    }

    /// <summary>
    /// returns the Sprite of the Entity for e.g. Cycling the animation
    /// </summary>
    /// <returns>the Sprite of the Entity</returns>
    public Sprite getSprite() {
        return mSprite;
    }


    /// <summary>
    /// returns the Collider of the Entity for e.g. Collision of Wireframe drawing
    /// </summary>
    /// <returns>the Collider of the Entity or Null if none</returns>
    public Collider getCollider() {return mCollider;}

    /// <summary>
    ///Sets the Position of the Entity to the new Position
    /// </summary>
    /// <returns>the Position of the Entity</returns>
    public dVec2D getPosition() {
        return mPosition;
    }

    /// <summary>
    /// Sets the Position of the Entity to the new Position
    /// </summary>
    /// <param name="position">the new Position of the Entity</param>
    public void setPosition(dVec2D position) {
        mPosition = position;
        // center the Collider on the Entities position
        if(mCollider != null)
            mCollider.moveBy(mPosition.getDifference(mCollider.getCenter()));
    }

    /// <summary>
    /// Sets the Velocity of the Entity to the given Velocity
    /// </summary>
    /// <param name="velocity">the new Velocity of the Entity</param>
    public void forceSetVelocity(dVec2D velocity) {
        mVelocity = velocity;
    }

    /// <summary>
    /// Sets the absolute maximum Velocity for the Entity to the given Value
    /// </summary>
    /// <param name="maxVelocity">the new Maximum Velocity</param>
    public void setMaxVelocity(double maxVelocity){
        maxAbsoluteVelocity = maxVelocity;
    }

    /// <summary>
    /// Returns the Velocity
    /// </summary>
    /// <returns>the current velocity of the Entity</returns>a
    public dVec2D getVelocity() {
        return mVelocity;
    }

    /// <summary>
    /// Updates the Velocity of the Entity based on the Forces that work on the Entity
    /// </summary>
    public void updateVelocity() {
        for(dVec2D force : mForces){
            mVelocity = mVelocity.getSum(force);
        }
        //scale velocity to be below maximum velocity
        if(mVelocity.abs() > maxAbsoluteVelocity)
            mVelocity.scale(maxAbsoluteVelocity / mVelocity.abs());
//        Log.d("Entity","Updated Velocity to " +mVelocity.toString());
    }

    /// <summary>
    /// Updates the Position of the Entity based on the Velocity
    /// </summary>
    /// <param name="elapsedTime">the Time between this and the previous frame</param>
    public void updatePosition(double elapsedTime) {
        dVec2D direction = mVelocity.getScaled(elapsedTime);
        mPosition = mPosition.getSum(direction);
        mCollider.moveBy(direction);
        /*
        Log.d("Entity","elapsed Time in UpdatePosition: "+elapsedTime+"\n" +
                "\tcurrent Velocity: "+mVelocity.toString()+"\n"+
                "\tnew Position: "+mPosition.toString());
         */
    }

    /// <summary>
    /// Handles any other Updates to the Entity
    /// </summary>
    public abstract void update(double elapsedTime);

}
