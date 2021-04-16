package com.example.noisitapp.JosepFortunyClasses.Helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.example.noisitapp.JosepFortunyClasses.VisualizerView;
import com.example.noisitapp.R;

/**
 * helper class to use VisualizerView directly
 * in a XML layout file.
 *
 * You can set the data via attribute <b>app:seriesData</b>
 * in the format: "X=Y;X=Y;..." e.g. "0=5.0;1=5;2=4;3=9"
 *
 * Other styling options:
 * <li>app:seriesType="line|bar|points"</li>
 * <li>app:seriesColor="#ff0000"</li>
 * <li>app:seriesTitle="foobar" - if this is set, the legend will be drawn</li>
 * <li>android:title="foobar"</li>
 *
 * Example:
 * <pre>
 * {@code
 *  <com.example.noisitApp.JosepFortunyClasses.Helper.VisualizerViewXML
 *      android:layout_width="match_parent"
 *      android:layout_height="100dip"
 *      app:seriesColor="#ee0000" />
 * }
 * </pre>
 *
 * @author Josep Fortuny Casablancas
 */
public class VisualizerViewXML extends VisualizerView {

    public VisualizerViewXML(Context context, AttributeSet attrs) {
        super(context, attrs);
        // get attributes
        @SuppressLint("CustomViewStyleable") TypedArray a=context.obtainStyledAttributes(
                attrs,
                R.styleable.GraphViewXML);
        String title = a.getString(R.styleable.GraphViewXML_android_title);
        a.recycle();
        if (title != null && !title.isEmpty()) {
            setTitle(title);
        }
    }
}
