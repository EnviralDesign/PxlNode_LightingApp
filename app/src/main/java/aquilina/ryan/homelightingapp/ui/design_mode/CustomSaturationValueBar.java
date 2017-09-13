package aquilina.ryan.homelightingapp.ui.design_mode;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.larswerkman.holocolorpicker.SVBar;

/**
 * Created by SterlingRyan on 9/13/2017.
 */

public class CustomSaturationValueBar extends SVBar {

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

    public CustomSaturationValueBar(Context context) {
        super(context);
        init();
    }

    public CustomSaturationValueBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomSaturationValueBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        setEnabled(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isEnabled()){
            return true;
        }
        return super.onTouchEvent(event);
    }
}
