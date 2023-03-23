package da.nzse.engine;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
    private final int cTargetFPS = 30;

    //private members and variables
    private final SurfaceHolder mSurfaceHolder;
    private final GameView mGameView;

    private boolean mRunning;
    private boolean mPaused;

    //public members and variables
    public static Canvas mCanvas;

    public GameThread(SurfaceHolder holder, GameView view) {
        super();
        mPaused = false;
        mSurfaceHolder = holder;
        mGameView = view;
    }

    /// <summary>
    /// the Main Execution Loop of the Game
    /// </summary>
    @Override public void run() {
        final boolean cFPSDisplay = false;
        //timing
        long tStart;
        long tElapsedTime = 0;

        long tFrameDuration = 0;
        long tFrameStart;
        long tTarget = (long)cTargetFPS * 1000 * 1000 * 1000;
        long tThreadWait;

        long tTotal = 0;
        int frameCount = 0;

        double mAverageFPS;

        // For some reason this turns out to be about every 4 seconds...
        // ... doesn't make sense why but ... hey
        final long interval = 4;
        final long intervalMul = (long)3e10;

        tStart = System.nanoTime();

        while(mRunning) {
            tElapsedTime += tFrameDuration;
            tFrameStart = System.nanoTime();

            mCanvas = null;

            try{
                mCanvas = mSurfaceHolder.lockCanvas();
                mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                synchronized (mSurfaceHolder) {
                    if(!mPaused) {
                        mGameView.update((double) tFrameDuration * 1e-9,
                                (double) tElapsedTime * 1e-9);
                        mGameView.draw(mCanvas);
                    }
                }
            } catch (Exception e){
                Log.w("GameThread", "Unexpected Exception in update/draw: " + e.toString());
            } finally {
                if(mCanvas != null)
                    try{
                        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                    } catch(Exception e){
                        e.printStackTrace();
                    }
            }
            tThreadWait = tTarget - ( System.nanoTime() - tFrameStart );
            long tThreadWaitMillis = tThreadWait/1000/1000/1000;

            try {
                if(tThreadWait > 0)
                    Thread.sleep(tThreadWaitMillis,0);
                else
                    Log.d("GameThread","Running "+(tTarget-tThreadWait)+"ns behind!");
            } catch (Exception e) {
                Log.w("GameThread", "Unexpected Exception in Sleep: " + e.toString());
            }

            if(cFPSDisplay) {
                tTotal += (System.nanoTime() - tStart);
                frameCount++;
                if(tTotal >= (interval*intervalMul)){
                    mAverageFPS = (double)frameCount / interval;
                    frameCount = 0;
                    tTotal -= (interval*intervalMul);
                    tStart = System.nanoTime();
                    Log.d("GameThread", "average FPS: "+mAverageFPS);
                }
            }

            tFrameDuration = System.nanoTime() - tFrameStart;

        }

    }

    /// <summary>
    /// Starts or Stops the game loop
    /// </summary>
    /// <param name="state">State of the Game (true=running, false=stopped)</param>
    public void setRunning(boolean state) {
        mRunning = state;
    }

    /// <summary>
    /// Sets the Paused state of the game to the given Value.
    /// true means the GameLoop will not update the Physics and Graphics
    /// </summary>
    /// <param name="state">set the Paused State</param>
    public void setPaused(boolean state){
        mPaused = state;
    }

}
