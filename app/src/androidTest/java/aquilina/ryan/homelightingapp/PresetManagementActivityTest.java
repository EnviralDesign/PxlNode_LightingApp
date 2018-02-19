/*
 * Created by Ryan Aquilina on 1/9/18 9:55 AM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2018.  All rights reserved
 *
 * Last modified 1/9/18 9:55 AM
 */

package aquilina.ryan.homelightingapp;

import android.support.test.espresso.PerformException;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import aquilina.ryan.homelightingapp.ui.group_managment.PresetManagementActivity;
import aquilina.ryan.homelightingapp.util.RecyclerViewItemCountAssertion;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PresetManagementActivityTest {

    @Rule
    public ActivityTestRule<PresetManagementActivity> mActivityRule = new ActivityTestRule<>(
            PresetManagementActivity.class);


    @Test
    public void deletePreset_checkListSize(){
        try {
            RecyclerView.Adapter adapter = ((RecyclerView) mActivityRule.getActivity().getWindow().getDecorView().findViewById(R.id.groups_recycler_list)).getAdapter();
            int recyclerViewSize = adapter.getItemCount();

            // Long click the first preset
            onView(withId(R.id.groups_recycler_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));

            // Click on the delete menu button
            onView(withId(R.id.delete_button)).perform(click());

            if (recyclerViewSize > 1) {

                // Check if list size has decreased by one
                onView(withId(R.id.groups_recycler_list)).check(new RecyclerViewItemCountAssertion(recyclerViewSize - 1));

            } else {

                // Check id error is shown
                onView(withText(R.string.text_view_create_presets)).check(matches(isDisplayed()));

            }
        } catch (PerformException e){

            // Check error message is shown if no presets exist.
            onView(withText(R.string.text_view_create_presets)).check(matches(isDisplayed()));

        }
    }

    @Test
    public void createAMacro_checkToastSuccessMessageIsDisplayed(){
        try{
            // Long click the first preset in the recycler view
            onView(withId(R.id.groups_recycler_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));

            // Click on save macro button
            onView(withId(R.id.save_macro_button)).perform(click());

            // Type the name of the new macro
            onView(withId(R.id.dialog_edit_text)).perform(typeText("Test Macro"));

            // Click on save button
            onView(withId(R.id.button2)).perform(click());

            // Check if group saved toast is shown
            onView(withText(R.string.toast_macro_saved)).inRoot(withDecorView(not(Matchers.is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

        } catch (PerformException e){

            // Check error message is shown if no presets exist.
            onView(withText(R.string.text_view_create_presets)).check(matches(isDisplayed()));

        }
    }
}
