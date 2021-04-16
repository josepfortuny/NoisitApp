package com.example.noisitapp.JosepFortunyClasses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.view.View;
import com.example.noisitapp.JosepFortunyClasses.Render.Renderer;

import java.util.HashSet;
import java.util.Set;

public class VisualizerView extends View {
    private byte[] mBytes;
    private byte[] mFFTBytes;
    private Paint lineColor = new Paint();
    private Rect mRect = new Rect();
    private Visualizer mVisualizer;
    private GridLabelRender mGridLabelRender;
    private Set<Renderer> mRenderers;
    private final Paint mFlashPaint = new Paint();
    private final Paint mFadePaint = new Paint();
    private final Paint mPaintTitle = new Paint();
    private String mTitle;
    private static final class Styles {
        /**
         * The font size of the title that can be displayed
         * above the graph.
         *
         */
        float titleTextSize;
        /**
         * The font color of the title that can be displayed
         * above the graph.
         */
        int titleColor;
    }

    public VisualizerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        init();
    }
    public VisualizerView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public VisualizerView(Context context)
    {
        this(context, null, 0);
    }

    private void init() {
        mBytes = null;
        mFFTBytes = null;
        mTitle = null;
        lineColor.setColor(Color.BLACK);
        mFlashPaint.setColor(Color.argb(122, 255, 255, 255));
        mFadePaint.setColor(Color.argb(238, 255, 255, 255)); // Adjust alpha to change how quickly the image fades
        mFadePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        mRenderers = new HashSet<Renderer>();
        mGridLabelRender = new GridLabelRender(this);
    }
    public void setTitle(String title){
        mTitle = title;
        String[] axis = title.split("/");
        mGridLabelRender.setmVerticalAxisTitle(axis[0]);
        mGridLabelRender.setmHorizontalAxisTitle(axis[1]);
    }

    public void link(MediaPlayer player) {
        if(player == null) {
            throw new NullPointerException("Cannot link to null MediaPlayer");
        }
        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(player.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        // Pass through Visualizer data to VisualizerView
        Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener()
        {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                updateVisualizer(bytes);
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                updateVisualizerFFT(bytes);
            }
        };

        mVisualizer.setDataCaptureListener(captureListener, Visualizer.getMaxCaptureRate() / 2, false, true);
        // Enabled Visualizer and disable when we're done with the stream
        mVisualizer.setEnabled(true);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer)
            {
                mVisualizer.setEnabled(false);
            }
        });
    }

    public void addRenderer(Renderer renderer) {
        if(renderer != null) {
            mRenderers.add(renderer);
        }
    }

    public void clearRenderers()
    {
        mRenderers.clear();
    }

    /**
     * Call to release the resources used by VisualizerView. Like with the
     * MediaPlayer it is good practice to call this method
     */
    public void release()
    {
        mVisualizer.release();
    }

    /**
     * Pass data to the visualizer. Typically this will be obtained from the
     * Android Visualizer.OnDataCaptureListener call back. See
     * {@link Visualizer.OnDataCaptureListener#onWaveFormDataCapture }
     * @param bytes
     */
    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        invalidate();
    }
    /**
     * Pass FFT data to the visualizer. Typically this will be obtained from the
     * Android Visualizer.OnDataCaptureListener call back. See
     * {@link Visualizer.OnDataCaptureListener#onFftDataCapture }
     * @param bytes
     */
    public void updateVisualizerFFT(byte[] bytes) {
        mFFTBytes = bytes;
        invalidate();
    }

    boolean mFlash = false;

    /**
     * Call this to make the visualizer flash. Useful for flashing at the start
     * of a song/loop etc...
     */
    public void flash() {
        mFlash = true;
        invalidate();
    }

    Bitmap mCanvasBitmap;
    Canvas mCanvas;


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawGraphtobePloted(canvas);
        drawTitle(canvas);
        mGridLabelRender.draw(canvas);

        if(mCanvasBitmap == null) {
            //mCanvasBitmap = Bitmap.createBitmap( ,400,400,getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            mCanvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        }
        if(mCanvas == null) {
            mCanvas = new Canvas(mCanvasBitmap);
        }

        if (mBytes != null) {
            // Render all audio renderers
            @SuppressLint("DrawAllocation") AudioData audioData = new AudioData(mBytes);
            for(Renderer r : mRenderers) {
                r.render(mCanvas, audioData, mRect);
            }
        }
        if (mFFTBytes != null) {
            // Render all FFT renderers
            @SuppressLint("DrawAllocation") FFTData fftData = new FFTData(mFFTBytes);
            for(Renderer r : mRenderers) {
                r.render(mCanvas, fftData, mRect);
            }
        }
        // Fade out old contents
        mCanvas.drawPaint(mFadePaint);

        if(mFlash) {
            mFlash = false;
            mCanvas.drawPaint(mFlashPaint);
        }
        canvas.drawBitmap(mCanvasBitmap, new Matrix(), null);
    }
    protected void drawTitle(Canvas canvas) {
        if (mTitle != null && mTitle.length()>0) {
            mPaintTitle.setColor(Color.BLACK);
            int titleTextSize = 60;
            mPaintTitle.setTextSize(titleTextSize);
            mPaintTitle.setTextAlign(Paint.Align.CENTER);
            float x = getWidth()/2;
            float y = mPaintTitle.getTextSize();
            canvas.drawText(mTitle, x, y, mPaintTitle);
        }
    }
    protected void drawGraphtobePloted(Canvas canvas){
        int graphPadding = 100;
        int lineWidth = 6;
        mRect.set (0,0 , 0, getHeight() - (graphPadding + lineWidth + 5));
    }
}

