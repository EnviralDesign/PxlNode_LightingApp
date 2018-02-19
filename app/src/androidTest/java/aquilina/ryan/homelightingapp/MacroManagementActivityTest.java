/*
 * Created by Ryan Aquilina on 1/9/18 10:24 AM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2018.  All rights reserved
 *
 * Last modified 1/9/18 10:24 AM
 */

package aquilina.ryan.homelightingapp;

import android.support.test.espresso.PerformException;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import aquilina.ryan.homelightingapp.ui.group_managment.MacroManagementActivity;
import aquilina.ryan.homelightingapp.util.RecyclerViewItemCountAssertion;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MacroManagementActivityTest {
    @Rule
    public ActivityTestRule<MacroManagementActivity> mActivityRule = new ActivityTestRule<>(
            MacroManagementActivity.class);


    @Test
    public void deleteMacro_checkListSize(){
        try {
            RecyclerView.Adapter adapter = ((RecyclerView) mActivityRule.getActivity().getWindow().getDecorView().findViewById(R.id.groups_recycler_list)).getAdapter();
            int recyclerViewSize = adapter.getItemCount();

            // Long click the first group
            onView(withId(R.id.groups_recycler_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));

            // Click on the delete menu button
            onView(withId(R.id.delete_button)).perform(click());

            if (recyclerViewSize > 1) {

                // Check if list size has decreased by one
                onView(withId(R.id.groups_recycler_list)).check(new RecyclerViewItemCountAssertion(recyclerViewSize - 1));

            } else {

                // Check id error is shown
                onView(withText(R.string.text_view_create_macros)).check(matches(isDisplayed()));

            }
        } catch (PerformException e){
            e.printStackTrace();
            onView(withText(R.string.text_view_create_macros)).check(matches(isDisplayed()));
        }
    }
}
