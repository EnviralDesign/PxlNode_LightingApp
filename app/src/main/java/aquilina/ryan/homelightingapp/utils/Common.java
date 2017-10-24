/*
 * Created by Ryan Aquilina on 10/18/17 5:15 PM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 10/16/17 3:50 PM
 */

package aquilina.ryan.homelightingapp.utils;

import android.content.Context;
import android.widget.Toast;

public class Common {

    public void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
