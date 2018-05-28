package frost.com.homelighting.ui.designmode;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ResizeWidthAnimation extends Animation {
    private View mView;
    private int mStartWidth;
    private int mEndWidth;

    public ResizeWidthAnimation(View view, int endWidth) {
        mView = view;
        mEndWidth = endWidth;
        mStartWidth = view.getWidth();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newWidth;
        if(mStartWidth > mEndWidth){ // width is decreased
            newWidth = mStartWidth - (int) ((mStartWidth - mEndWidth) * interpolatedTime);
        } else { // width is increased
            newWidth = mStartWidth + (int) ((mEndWidth - mStartWidth) * interpolatedTime);
        }

        mView.getLayoutParams().width = newWidth;
        mView.requestLayout();
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }


}