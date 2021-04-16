package com.example.noisitapp.JosepFortunyClasses;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class GridLabelRender {

    /**
     * wrapper for the styles regarding
     * to the grid and the labels
     */

    private float padding = 20;
    private final float textLabelSize = 50;
    private final int graphPadding = 100;
    private final int lineWidth = 5;
    /**
     * reference to graphview
     */
    private VisualizerView mVisualizerView;
    /**
     * the paint to draw the grid lines
     */
    private Paint mPaintLine = new Paint();
    /**
     * the paint to draw axis titles
     */
    private Paint mPaintAxisTitle = new Paint();
    /**
     * the title of the horizontal axis
     */
    private String mHorizontalAxisTitle;
    /**
     * the title of the vertical axis
     */
    private String mVerticalAxisTitle;
    /**
     * count of the vertical labels, that
     * will be shown at one time.
     */
    private int mNumVerticalLabels;
    /**
     * count of the horizontal labels, that
     * will be shown at one time.
     */
    private int mNumHorizontalLabels;
    public GridLabelRender(VisualizerView visualizerView) {
        mVisualizerView = visualizerView;
        mPaintAxisTitle.setColor(Color.BLACK);
        mPaintAxisTitle.setTextSize(textLabelSize);
        mPaintLine.setColor(Color.BLACK);
        mPaintLine.setStrokeWidth(5);
        padding = 10;
    }
    public void setmHorizontalAxisTitle(String mHorizontalAxisTitle) {
        this.mHorizontalAxisTitle = mHorizontalAxisTitle;
    }
    public void setmVerticalAxisTitle(String mVerticalAxisTitle) {
        this.mVerticalAxisTitle = mVerticalAxisTitle;
    }
    /**
     * do the drawing of the grid
     * and labels
     * @param canvas canvas
     */
    public void draw(Canvas canvas) {
        drawHorizontalAxisTitle(canvas);
        drawVerticalAxisTitle(canvas);
        drawVerticalAxis(canvas);
        drawHorizontalAxis(canvas);

    }
    public void drawVerticalAxis(Canvas canvas){
        // Y Axis
        canvas.drawLine(graphPadding,graphPadding, (graphPadding),canvas.getHeight()- graphPadding,mPaintLine);
    }
    public void drawHorizontalAxis(Canvas canvas){
        // X Axis
        canvas.drawLine(graphPadding,  canvas.getHeight() - graphPadding, canvas.getWidth() - graphPadding, canvas.getHeight() - graphPadding , mPaintLine);
    }
    /**
     * draws the horizontal axis title if
     * it is set
     * @param canvas canvas
     */
    public void drawHorizontalAxisTitle(Canvas canvas) {
        if (mHorizontalAxisTitle != null && mHorizontalAxisTitle.length() > 0) {
            float x = canvas.getWidth() / 2;
            float y = canvas.getHeight() - padding;
            Log.e("Prova","Title Horizontal" + mHorizontalAxisTitle);
            canvas.drawText(mHorizontalAxisTitle, x, y, mPaintAxisTitle);
        }
    }
    /**
     * draws the vertical axis title if
     * it is set
     * @param canvas canvas
     */
    public void drawVerticalAxisTitle(Canvas canvas) {
        if (mVerticalAxisTitle != null && mVerticalAxisTitle.length() > 0) {
            float x = textLabelSize;
            float y = canvas.getHeight() / 2;
            canvas.save();
            canvas.rotate(-90, x, y);
            canvas.drawText(mVerticalAxisTitle, x, y, mPaintAxisTitle);
            canvas.restore();
        }
    }
}
