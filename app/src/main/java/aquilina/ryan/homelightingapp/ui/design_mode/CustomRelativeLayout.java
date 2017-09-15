package aquilina.ryan.homelightingapp.ui.design_mode;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by SterlingRyan on 9/13/2017.
 */

public class CustomRelativeLayout extends RelativeLayout {
    private boolean isEnabled;

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if(enabled){
            setAlpha(1);
        }
        else{
            setAlpha(0.2f);
        }
        this.isEnabled = enabled;
    }

    public CustomRelativeLayout(Context context) {
        super(context);
        init();
    }

    public CustomRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CustomRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        setEnabled(false);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(!isEnabled()){
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
