package aquilina.ryan.homelightingapp.ui.design_mode;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import aquilina.ryan.homelightingapp.R;

/**
 * Created by SterlingRyan on 9/11/2017.
 */

public class EffectsTimelineView extends ViewGroup implements ViewGroup.OnClickListener {
    private Timeline mTimeLineView;
    private CircleView mStartCircleView;
    private CircleView mStopCircleView;
    private CaptionView mStartCaptionView;
    private CaptionView mEndCaptionView;

    private int mStartCircleColor;
    private int mEndCircleColor;
    private int mLineColor;

    public EffectsTimelineView(Context context) {
        super(context);
        init();
    }

    public EffectsTimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.EffectsTimelineView,
                0, 0
        );

        try{
            mStartCircleColor = a.getColor(R.styleable.EffectsTimelineView_startColor, Color.GRAY);
            mEndCircleColor = a.getColor(R.styleable.EffectsTimelineView_endColor, Color.GRAY);
            mLineColor = a.getColor(R.styleable.EffectsTimelineView_lineColor, getResources().getColor(R.color.colorPrimary));
        }
        finally {
            a.recycle();
        }

        init();
    }

    @Override
    protected void onLayout(boolean b, int l, int i1, int i2, int i3) {
        final int count = getChildCount();
        int curWidth, curHeight, curLeft, curTop, maxHeight;

        // get available size of child view, without padding
        final int childLeft = this.getPaddingLeft();
        final int childTop = this.getPaddingTop();
        final int childRight = this.getMeasuredWidth() - this.getPaddingRight();
        final int childBottom = this.getMeasuredHeight() - this.getPaddingBottom();
        final int childWidth = ((childRight - childLeft) / 8) * 3;
        final int childHeight = childBottom - childTop;

        for (int i = 0; i < count; i++){
            View child = getChildAt(i);

            // Get maximum size of child
            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST));
            curWidth = child.getMeasuredWidth();
            curHeight = child.getMeasuredHeight();
            //do layout

            if(i == 0){
                child.layout(childLeft, childTop, childRight,childHeight);
            } else if(i == 1){
                child.layout(childLeft + dipToPixels(getContext(),10), childTop, curWidth + childLeft, curHeight + childTop);
            } else if(i == 2){
                child.layout(childRight - childWidth, childTop, childRight - dipToPixels(getContext(),10), curHeight + childTop);
            } else if(i == 3){
                child.layout(childLeft + dipToPixels(getContext(),10), (childHeight / 4) * 3,  curWidth + childLeft, childHeight);
            } else if(i == 4){
                child.layout(childRight - childWidth, (childHeight / 4) * 3,  childRight - dipToPixels(getContext(),10), childHeight);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view == mStartCircleView){
            if(mStopCircleView.isColorChanged){
                if(mStartCircleView.isSelected()){
                    unSelectView(mStartCircleView);
                } else {
                    selectView(mStartCircleView);
                    if(mStopCircleView.isSelected()){
                        unSelectView(mStopCircleView);
                    }
                }
            }
        } else if(view == mStopCircleView){
            if(mStopCircleView.isSelected()){
                unSelectView(mStopCircleView);
            } else {
                selectView(mStopCircleView);
                if(mStartCircleView.isSelected()){
                    unSelectView(mStartCircleView);
                }
            }
        }
    }

    public void selectView(View view){
        view.animate().scaleX(1.25f).scaleY(1.25f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(50);
        view.setSelected(true);
    }

    public void unSelectView(View view){
        view.animate().scaleX(1f).scaleY(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(50);
        view.setSelected(false);
    }

    public void changeStartCircleColor(int color){
        if(mStartCircleView.isSelected()){
            mStartCircleView.setCircleColor(color);
            mStartCircleView.invalidate();
        }
    }

    public void changeStopCircleColor(int color){
        if(mStopCircleView.isSelected()){
            mStopCircleView.setCircleColor(color);
            mStopCircleView.setColorChanged(true);
            mStartCircleView.setInFocus(true);
            mStopCircleView.invalidate();
        }
    }

    public void refreshView(){
        mStartCircleView.setCircleColor(getResources().getColor(R.color.colorPrimary));
        mStopCircleView.setCircleColor(getResources().getColor(R.color.colorPrimary));

        mStartCircleView.setInFocus(false);
        mStopCircleView.setInFocus(true);

        mStopCircleView.setColorChanged(false);
        selectView(mStopCircleView);
    }

    public void automaticFocus(){
        if(mStopCircleView.isInFocus){
            onClick(mStartCircleView);
        }
    }

    private void init(){
        mTimeLineView = new Timeline(getContext());
        mStartCircleView= new CircleView(getContext());
        mStopCircleView = new CircleView(getContext());
        mStartCaptionView = new CaptionView(getContext());
        mEndCaptionView = new CaptionView(getContext());

        mStartCaptionView.setCaptionText("Start Color");
        mEndCaptionView.setCaptionText("End Color");

        mStartCircleView.setClickable(true);
        mStopCircleView.setClickable(true);

        mStopCircleView.setOnClickListener(this);
        mStartCircleView.setOnClickListener(this);

        mStopCircleView.setInFocus(true);
        selectView(mStopCircleView);

        addView(mTimeLineView);
        addView(mStartCircleView);
        addView(mStopCircleView);
        addView(mStartCaptionView);
        addView(mEndCaptionView);
    }

    private class Timeline extends View{
        private final int DEFAULT_LINE_COLOR = Color.GRAY;

        private int lineColor = DEFAULT_LINE_COLOR;
        private Paint paintVertical;
        private Paint paintHorizontal;

        public Timeline(Context context) {
            super(context);

            lineColor = mLineColor;
            init(context);
        }

        public Timeline(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);

            lineColor = mLineColor;
            init(context);
        }

        private void init(Context context){
            paintVertical = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintHorizontal = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintVertical.setColor(lineColor);
            paintHorizontal.setColor(lineColor);
            paintVertical.setStrokeWidth(dipToPixels(context,2));
            paintHorizontal.setStrokeWidth(dipToPixels(context,1.5f));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int w = getWidth();
            int h = getHeight();

            canvas.drawLine(0, h/2, w, h/2, paintHorizontal);
            canvas.drawLine(0, h/4, 0, (h/4)*3, paintVertical);
            canvas.drawLine(w, h/4, w, (h/4)*3, paintVertical);
        }
    }

    public class CircleView extends View{
        private final int DEFAULT_CIRCLE_COLOR = Color.GRAY;

        private int circleColor = DEFAULT_CIRCLE_COLOR;
        private Paint paint;
        private Paint borderPaint;
        private DashPathEffect dashPathEffect;
        private boolean isInFocus = false;
        private boolean isColorChanged = false;

        public CircleView(Context context)
        {
            super(context);

            circleColor = mStartCircleColor;
            init(context, null);
        }

        public CircleView(Context context, AttributeSet attrs)
        {
            super(context, attrs);

            circleColor = mStartCircleColor;
            init(context, attrs);
        }

        private void init(Context context, AttributeSet attrs)
        {
            paint = new Paint();
            borderPaint = new Paint();
            paint.setAntiAlias(true);
            borderPaint.setAntiAlias(true);
            dashPathEffect =  new DashPathEffect(new float[] {10,10}, 0);
        }

        public void setCircleColor(int circleColor)
        {
            this.circleColor = circleColor;
            invalidate();
        }

        public int getCircleColor()
        {
            return circleColor;
        }

        public boolean isInFocus() {
            return isInFocus;
        }

        public void setInFocus(boolean inFocus) {
            isInFocus = inFocus;
            invalidate();
        }

        public boolean isColorChanged() {
            return isColorChanged;
        }

        public void setColorChanged(boolean colorChanged) {
            isColorChanged = colorChanged;
        }

        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);

            int w = getWidth();
            int h = getHeight();

            int pl = getPaddingLeft();
            int pr = getPaddingRight();
            int pt = getPaddingTop();
            int pb = getPaddingBottom();

            int usableWidth = w - (pl + pr);
            int usableHeight = h - (pt + pb);

            int radius = Math.min(usableWidth, usableHeight) / 2;
            int cx = pl + (usableWidth / 2);
            int cy = pt + (usableHeight / 2);

            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setColor(DEFAULT_CIRCLE_COLOR);
            borderPaint.setStrokeWidth(3);

            if(!isInFocus){
                borderPaint.setPathEffect(dashPathEffect);
            } else{
                borderPaint.setPathEffect(null);
            }
            canvas.drawCircle(cx, cy, radius - 2, borderPaint);

            paint.setColor(circleColor);
            canvas.drawCircle(cx, cy, radius - 3, paint);
        }
    }

    public class CaptionView extends View{

        private Paint textPaint = new Paint();
        private Rect rect = new Rect();

        private String captionText = "";

        public CaptionView(Context context) {
            super(context);
        }

        public CaptionView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public String getCaptionText() {
            return captionText;
        }

        public void setCaptionText(String captionText) {
            this.captionText = captionText;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int w = getWidth();
            int h = getHeight();

            int pl = getPaddingLeft();
            int pr = getPaddingRight();
            int pt = getPaddingTop();
            int pb = getPaddingBottom();

            int usableWidth = w - (pl + pr);
            int usableHeight = h - (pt + pb);

            canvas.getClipBounds(rect);
            int cHeight = rect.height();
            int cWidth = rect.width();
            textPaint.setColor(getResources().getColor(R.color.colorSecondaryText));
            textPaint.setAntiAlias(true);
            textPaint.setTextAlign(Paint.Align.LEFT);
            textPaint.setTextSize(spToPixels(getContext(),12));
            textPaint.getTextBounds(captionText, 0, captionText.length(),rect);
            float x = cWidth / 2f - rect.width() / 2f - rect.left;
            float y = cHeight / 2f + rect.height() / 2f - rect.bottom;
            canvas.drawText(captionText, x, y , textPaint);
        }
    }

    public static int dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static int spToPixels(Context context, float sp) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
        return px;
    }
}
