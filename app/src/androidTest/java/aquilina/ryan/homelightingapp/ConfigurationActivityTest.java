/*
 * Created by Ryan Aquilina on 2/26/18 2:01 PM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2018.  All rights reserved
 *
 * Last modified 2/26/18 2:01 PM
 */

package aquilina.ryan.homelightingapp;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import aquilina.ryan.homelightingapp.model.Device;
import aquilina.ryan.homelightingapp.model.OnlineDevices;
import aquilina.ryan.homelightingapp.ui.configuration_mode.ConfigurationActivity;
import aquilina.ryan.homelightingapp.ui.main_activity.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ConfigurationActivityTest {

    @Rule
    public ActivityTestRule<ConfigurationActivity> mActivityRule = new
            ActivityTestRule<>(ConfigurationActivity.class);

    @Before
    public void beforeTests(){
        ArrayList<Device> devices = new ArrayList<>();
        devices.add(new Device("testMock", "0.0.0.0"));
        mActivityRule.getActivity().mOnlineDevicesList = devices;
    }

    @Test
    public void inputText_validationMessage(){
        // Add mock data to the activity

        final EditText titleInput = mActivityRule.getActivity().findViewById(R.id.device_name_edit_text);
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                titleInput.setText("Hello World");
            }
        });

        // Check edit text error message is displayed.
        onView(withText(R.string.configuration_error_special_characters)).check(matches(isDisplayed()));

        // Click on the update device button.
        onView(withId(R.id.update_device_button)).perform(click());

        // Check that the update button sends out an invalid error toast message.
        onView(withText(R.string.configuration_error_invalid_input))
                .inRoot(withDecorView(not(Matchers.is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }
}
