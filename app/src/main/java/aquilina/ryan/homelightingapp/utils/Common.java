package aquilina.ryan.homelightingapp.utils;


import android.content.Context;
import android.widget.Toast;

/**
 * Created by SterlingRyan on 10/2/2017.
 */

public class Common {

    public void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
