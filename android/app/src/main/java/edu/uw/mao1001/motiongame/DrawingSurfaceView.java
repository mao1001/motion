package edu.uw.mao1001.motiongame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import java.util.HashMap;

/**
 * Created by Nick on 5/13/2016.
 */
public class DrawingSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "SurfaceView";

    private SurfaceHolder mHolder;
    private Thread mThread;
    private DrawingRunnable mRunnable;

    public HashMap<Integer, Panel> panels;

    private Point screenSize;

    private Paint panelPaint;
    private Paint panelCompletePaint;
    private Paint panelDisabledPaint;
    private Paint textPaint;
    private Paint textDisabledPaint;

    public DrawingSurfaceView(Context context) {
        this(context, null);
    }

    public DrawingSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawingSurfaceView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);

        mHolder = getHolder();
        mHolder.addCallback(this);

        mRunnable = new DrawingRunnable();
//
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        screenSize = new Point();
        display.getSize(screenSize);


        panelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        panelPaint.setColor(Color.RED);
        panelPaint.setStyle(Paint.Style.FILL);

        panelCompletePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        panelCompletePaint.setColor(Color.GREEN);
        panelPaint.setStyle(Paint.Style.FILL);

        panelDisabledPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        panelCompletePaint.setColor(Color.GRAY);
        panelCompletePaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(100);
        textPaint.setTextAlign(Paint.Align.CENTER);

        textDisabledPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textDisabledPaint.setColor(Color.WHITE);
        textDisabledPaint.setTextSize(50);
        textDisabledPaint.setTextAlign(Paint.Align.CENTER);

        panels = new HashMap<>();

        int panelAmount = 3;
        int width = screenSize.x;
        int height = screenSize.y;
        for (int i = 0; i < panelAmount; i++) {
            int left = (width / panelAmount) * i;
            int top = 0;
            int bottom = height;
            int right = (width / panelAmount) * (i + 1);

            Panel temp = new Panel(new Rect(left, top, right, bottom));

            panels.put(i, temp);
        }


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "Creating new drawing thread");
        mThread = new Thread(mRunnable);
        mRunnable.setRunning(true); //turn on the runner
        mThread.start(); //start up the thread when surface is created
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        mRunnable.setRunning(false); //turn off
        boolean retry = true;
        while(retry) {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {
                //will try again...
            }
        }
        Log.d(TAG, "Drawing thread shut down");
    }

    public void update() {

    }

    public synchronized void render(Canvas canvas) {
        if (canvas == null) return;

        canvas.drawColor(Color.rgb(51,10,111)); //purple out the background

        //Log.d(TAG, "About to draw thing");

        for (int i = 0; i < 3; i++) {
            Panel panel = panels.get(i);
            if (panel.enabled) {
                if (panel.isMatched()) {
                    canvas.drawRect(panel.canvas, panelCompletePaint);

                } else {
                    canvas.drawRect(panel.canvas, panelPaint);
                }

                canvas.drawText(panel.target + " or " + -panel.target, panel.canvas.exactCenterX(), panel.canvas.exactCenterY() - 70, textPaint);
                canvas.drawText(panel.getDegreesInString(), panel.canvas.exactCenterX(), panel.canvas.exactCenterY() + 70, textPaint);
            } else {
                canvas.drawRect(panel.canvas, panelDisabledPaint);
                canvas.drawText(getContext().getString(R.string.status_disabled), panel.canvas.exactCenterX(), panel.canvas.exactCenterY(), textDisabledPaint);

            }
        }
    }


    /**
     * An inner class representing a runnable that does the drawing. Animation timing could go in here.
     * http://obviam.net/index.php/the-android-game-loop/ has some nice details about using timers to specify animation
     */
    public class DrawingRunnable implements Runnable {

        private boolean isRunning; //whether we're running or not (so we can "stop" the thread)

        public void setRunning(boolean running){
            this.isRunning = running;
        }

        public void run() {
            Canvas canvas;
            while(isRunning)
            {
                canvas = null;
                try {
                    canvas = mHolder.lockCanvas(); //grab the current canvas
                    synchronized (mHolder) {
                        update(); //update the game
                        render(canvas); //redraw the screen
                    }
                }
                finally { //no matter what (even if something goes wrong), make sure to push the drawing so isn't inconsistent
                    if (canvas != null) {
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}
