/*
 * Created by Ryan Aquilina on 3/26/18 9:45 AM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2018.  All rights reserved
 *
 * Last modified 3/26/18 9:45 AM
 */

package frost.com.homelighting.ui.configuration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

public class ColorBoxView extends View {

    private final int DEFAULT_BOX_COLOR = Color.GRAY;

    private int boxColor = DEFAULT_BOX_COLOR;
    private Paint paint;
    private Paint borderPaint;

    public ColorBoxView(Context context) {
        super(context);
        init();
    }

    public ColorBoxView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorBoxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        paint = new Paint();
        borderPaint = new Paint();
   }

   public void setBoxColor(int boxColor){
        this.boxColor = boxColor;
        invalidate();
   }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(boxColor);

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.GRAY);
        borderPaint.setStrokeWidth(3);

        canvas.drawRoundRect(getLeft(), getTop(), getRight(), getBottom(), dipToPixels(getContext(),6), dipToPixels(getContext(),6), paint);
        canvas.drawRoundRect(getLeft(), getTop(), getRight(), getBottom(), 10, 10, borderPaint);
    }

    public static int dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
