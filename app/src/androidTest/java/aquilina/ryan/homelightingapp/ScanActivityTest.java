/*
 * Created by Ryan Aquilina on 10/18/17 5:15 PM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 9/28/17 3:26 PM
 */

package aquilina.ryan.homelightingapp;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.espresso.util.TreeIterables;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import aquilina.ryan.homelightingapp.ui.scan_mode.ScanActivity;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ScanActivityTest {

    @Rule
    public ActivityTestRule<ScanActivity> mActivityRule = new ActivityTestRule<>(
            ScanActivity.class
    );

    /**
     * Check that refresh button works, this test
     * needs at least one online device to pass.
     */
    @Test
    public void refreshItems_checkIfErrorIsNotDisplayed(){
        // Click on the refresh devices menu item.
        onView(withId(R.id.refresh)).perform(click());

        //Progress dialogue is showing
        try{
            Thread.sleep(2000);
        } catch (Exception e){
            e.printStackTrace();
        }

        // Check if a device is scanned
        onView(withId(R.id.no_devices_found)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void clickOnItemsAddToGroup_checkGroup(){
        // Click on the first device
        onView(withId(R.id.devices_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

        onView(withId(R.id.add_to_group_fab))
                .perform(click());

        onView(withText("Save Group")).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnSaveGroupButton_checkToastIsDisplayed(){

        // Click on the first device
        onView(withId(R.id.devices_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

        // Start save group dialog.
        onView(withId(R.id.add_to_group_fab))
                .perform(click());

        // Enter a group name
        onView(withId(R.id.dialog_edit_text))
                .perform(typeText("Test Group"));

        // Click on save group button.
        onView(withId(R.id.button2))
                .perform(click());

        // Check if group saved toast is shown
        onView(withText(R.string.toast_group_saved)).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

    }

    @Test
    public void rejectNullGroupName_checkToastIsNotDisplayed(){
        // Click on the first device
        onView(withId(R.id.devices_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

        // Start save group dialog.
        onView(withId(R.id.add_to_group_fab))
                .perform(click());

        // Enter a group name
        onView(withId(R.id.dialog_edit_text))
                .perform(typeText(" "));

        // Click on save group button.
        onView(withId(R.id.button2))
                .perform(click());

        // Check if group saved toast is shown
        onView(withText(R.string.toast_group_saved)).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(doesNotExist());
    }

//    @Test
//    public void scrollItemsDown_checkItemsLoad(){
//        // Scroll the recycler view down, the app must have
//        // more than 9 devices turned on.
//        Espresso.onView(ViewMatchers.withId(R.id.devices_list_view))
//                .perform(RecyclerViewActions.actionOnItemAtPosition(14, ViewActions.click()));
//
//        String deviceName = "Light Name";
//        onView(withId(R.id.devices_list_view)).check(matches(hasDescendant(withText(deviceName))));
//    }

}
