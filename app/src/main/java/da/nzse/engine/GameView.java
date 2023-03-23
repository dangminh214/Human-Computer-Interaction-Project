package da.nzse.engine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    public GameThread mThread;
    private Ninja mCharacter;
    private Level mLevel;

    private dVec2D mGravity = new dVec2D(0,250);

    private double mGameSpeed = 300;

    private double mLastSpriteChange = 0;
    private final double mSpriteChangeInterval = .05;

    private long mScore = 0;

    public void setmGameSpeed(double mGameSpeed) {
        this.mGameSpeed = mGameSpeed;
    }

    private GameViewEventListener mListener = new GameViewEventListener() {
        @Override
        public void onGameOver(GameView gameView) {

        }

        @Override
        public void onScoreChange(long currentScore) {

        }
    };
    private boolean mGameOver = false;

    public GameView(Context context){
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attributes){
        super(context, attributes);
        init(context);
    }

    public GameView(Context context, AttributeSet attributes, int style) {
        super(context, attributes, style);
        init(context);
    }

    private void init(Context context) {
        getHolder().addCallback(this);

        setBackgroundColor(Color.TRANSPARENT);
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);

        mThread = new GameThread(getHolder(), this);
        setFocusable(true);

        mLevel = new Level(context);

        mCharacter = new Ninja(context);
        mCharacter.setPosition(new dVec2D(300,600));
        mCharacter.mForces.add(mGravity);
        mCharacter.getSprite().setState(0,1);

        Log.d("GameView","Initialization finished");

    }

    public void setListener(GameViewEventListener listener) { mListener = listener; }

    public void restart() {
        mThread = new GameThread(getHolder(), this);
    }

    /// <summary>
    /// Sets the pause state of the Game to the given State
    /// </summary>
    /// <param name="state">true to pause the game, false to unpause</param>
    public void pause(boolean state){
        mThread.setPaused(state);
    }

    public void update(double frameTime, double elapsedTime) {
        if(mGameOver)return;
        mLastSpriteChange += frameTime;
        mListener.onScoreChange(mScore);

        //check for Collision with Level Geometry and adjust character position and velocity
        dVec2D resolve = mLevel.checkGeometryCollision(mCharacter);
        if(resolve != null){
            mCharacter.setPosition(mCharacter.getPosition().getSum(new dVec2D(0,resolve.y)));
//            if(mCharacter.getVelocity().y * resolve.y < 0)
                mCharacter.forceSetVelocity(new dVec2D(mCharacter.getVelocity().x, 0));
        }

        //check for character death by hole
        if((mCharacter.getPosition().y < 0 ||
            mCharacter.getPosition().y > getHeight())){
            Log.d("GameView","Game Over");
            mGameOver = true;
            mListener.onGameOver(this);
        }

        //check for character death by death zone
        if( mLevel.checkDeathCollision(mCharacter)){
            Log.d("GameView","Game Over");
            mGameOver = true;
            mListener.onGameOver(this);
        }

        mCharacter.updateVelocity();
        mCharacter.updatePosition(frameTime);
        if(mLastSpriteChange >= mSpriteChangeInterval){
            mCharacter.getSprite().cycleNextColumn();
            mLastSpriteChange -= mSpriteChangeInterval;
            mScore++;
        }

        mLevel.moveLeftBy(frameTime*mGameSpeed);
    }

    @Override public void surfaceCreated(@NonNull SurfaceHolder holder) {
        mThread.setRunning(true);
        mThread.start();
    }

    @Override public void surfaceChanged(@NonNull SurfaceHolder holder, int _1, int _2, int _3) {}

    @Override public void surfaceDestroyed(@NonNull SurfaceHolder holder){
        try {
            mThread.setRunning(false);
            mThread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override public void draw(Canvas canvas){
        super.draw(canvas);
        if(mGameOver) return;

        mLevel.draw(canvas, false);

        mCharacter.draw(canvas);
        Collider c = mCharacter.getCollider();

        /*
        for(Collider collider : mLevel.getClosestSegment(mCharacter).getGeometryColliders()){
            c.draw(canvas,collider,true);
        }
        */
    }

    @Override public boolean onTouchEvent(MotionEvent event){
        int x = (int)event.getX();
        int y = (int)event.getY();
        Log.d("GameView", "pos: "+(new dVec2D(x,y)).toString());

        mGravity.scale(-1);
        if(mGravity.y > 0)
            mCharacter.getSprite().cycleNextRow();
        else
            mCharacter.getSprite().cyclePreviousRow();

        return super.onTouchEvent(event);
    }
}
