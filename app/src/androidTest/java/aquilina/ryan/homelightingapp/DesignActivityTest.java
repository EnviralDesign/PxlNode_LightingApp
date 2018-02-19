/*
 * Created by Ryan Aquilina on 10/18/17 5:15 PM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 9/28/17 3:26 PM
 */

package aquilina.ryan.homelightingapp;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import aquilina.ryan.homelightingapp.ui.design_mode.DesignActivity;
import fr.ganfra.materialspinner.MaterialSpinner;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DesignActivityTest {

    @Rule
    public ActivityTestRule<DesignActivity> mActivityRule = new ActivityTestRule<>(
            DesignActivity.class
    );

    @Test
    public void selectGroupItem_checkEffectControls(){
        // Click on the material spinner.
        onView(withId(R.id.item_spinner))
                .perform(click());

        // Click on a device.
        onData(allOf(is(instanceOf(Object.class)))).atPosition(0).perform(click());

        // Check if color picker is displayed.
        onView(withId(R.id.picker)).check(matches(isDisplayed()));
    }

    @Test
    public void selectEffectItem_checkEffectControls(){
        // Click on the material spinner
        onView(withId(R.id.item_spinner))
                .perform(click());

        // Click on a device
        onData(allOf(is(instanceOf(Object.class)))).atPosition(0).perform(click());

        // Enable an effect
        onView(withId(R.id.blink_button))
                .perform(click());

        // Check if effects timeline is shown
        onView(withId(R.id.effects_timeline)).check(matches(isDisplayed()));
    }

    @Test
    public void preventSave_checkDialogDoesNotExist(){
        // Click on the material spinner.
        onView(withId(R.id.item_spinner))
                .perform(click());

        // Click on a device.
        onData(allOf(is(instanceOf(Object.class)))).atPosition(0).perform(click());

        // Enable an effect
        onView(withId(R.id.blink_button))
                .perform(click());

        // Click on the save preset button
        onView(withId(R.id.save_preset_button)).perform(click());

        // Check if dialog exists
        onView(withId(R.id.parentPanel)).check(doesNotExist());
    }

    @Test
    public void preventPreview_checkPreviewButtonIsEnabled(){
        // Click on the material spinner.
        onView(withId(R.id.item_spinner))
                .perform(click());

        // Click on a device.
        onData(allOf(is(instanceOf(Object.class)))).atPosition(0).perform(click());

        // Enable an effect.
        onView(withId(R.id.blink_button))
                .perform(click());

        // Check if preset button is enabled.
        onView(withId(R.id.preview_preset_button)).check(matches(not(isEnabled())));
    }

    @Test
    public void previewACommand_checkPreviewButtonIsEnabled(){
        // Click on the material spinner.
        onView(withId(R.id.item_spinner))
                .perform(click());

        // Click on a device.
        onData(allOf(is(instanceOf(Object.class)))).atPosition(0).perform(click());

        // Enable an effect.
        onView(withId(R.id.blink_button))
                .perform(click());

        // Move the color pointer
        onView(withId(R.id.picker)).perform(dragColorWheelPointerFromTopToRight());

        // Check if preset button is enabled.
        onView(withId(R.id.preview_preset_button)).check(matches(isEnabled()));
    }

    @Test
    public void savePreset_checkToastMessageIsShown(){
        // Click on the material spinner.
        onView(withId(R.id.item_spinner))
                .perform(click());

        // Click on a device.
        onData(allOf(is(instanceOf(Object.class)))).atPosition(0).perform(click());

        // Enable an effect.
        onView(withId(R.id.blink_button))
                .perform(click());

        // Move the color pointer
        onView(withId(R.id.picker)).perform(dragColorWheelPointerFromTopToRight());

        // Choose the duration amount
        onView(withId(R.id.duration_picker)).perform(dragViewFromBottomToTop());

        // Choose the number of repetitions
        onView(withId(R.id.repetitions_picker)).perform(dragViewFromTopToBottom());

        // Click on the start circle
        onView(withId(R.id.effects_timeline)).perform(clickOnStartCircle());

        // Move the color pointer from right to bottom
        onView(withId(R.id.picker)).perform(dragColorWheelPointerFromRightToBottom());

        // Click on save preset button
        onView(withId(R.id.save_preset_button)).perform(click());

        // Type the name of the preset
        onView(withId(R.id.dialog_edit_text)).perform(typeText("Test Preset"));

        // Click on save button
        onView(withId(R.id.button2)).perform(click());

        // Check if group saved toast is shown
        onView(withText(R.string.toast_preset_saved)).inRoot(withDecorView(not(Matchers.is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

    }


    @Test
    public void noChangesWhenDeviceChanged_checkPickersAndButtonStates(){
        // This test must need at least 2 devices that are online

        // Click on the material spinner.
        onView(withId(R.id.item_spinner))
                .perform(click());

        // Click on a device.
        onData(allOf(is(instanceOf(Object.class)))).atPosition(0).perform(click());

        // Enable an effect.
        onView(withId(R.id.blink_button))
                .perform(click());

        // Move the color pointer
        onView(withId(R.id.picker)).perform(dragColorWheelPointerFromTopToRight());

        // Choose the duration amount
        onView(withId(R.id.duration_picker)).perform(dragViewFromBottomToTop());

        // Choose the number of repetitions
        onView(withId(R.id.repetitions_picker)).perform(dragViewFromTopToBottom());

        // Click on the start circle
        onView(withId(R.id.effects_timeline)).perform(clickOnStartCircle());

        // Move the color pointer from right to bottom
        onView(withId(R.id.picker)).perform(dragColorWheelPointerFromRightToBottom());

        // Click on the material spinner.
        onView(withId(R.id.item_spinner))
                .perform(click());

        // Change the device pressed
        onData(allOf(is(instanceOf(Object.class)))).atPosition(1).perform(click());

        // Check if save button is enabled
        onView(withId(R.id.save_preset_button)).check(matches(isEnabled()));

        // Check if preset button is enabled
        onView(withId(R.id.preview_preset_button)).check(matches(isEnabled()));

        // Check if blink button is in focus
        onView(withId(R.id.blink_button)).check(matches(isSelected()));

    }

    @Test
    public void saveActivityState_checkEffectsDidNotChange(){
        // Click on the material spinner.
        onView(withId(R.id.item_spinner))
                .perform(click());

        // Click on a device.
        onData(allOf(is(instanceOf(Object.class)))).atPosition(0).perform(click());

        // Enable an effect.
        onView(withId(R.id.blink_button))
                .perform(click());

        // Move the color pointer
        onView(withId(R.id.picker)).perform(dragColorWheelPointerFromTopToRight());

        // Choose the duration amount
        onView(withId(R.id.duration_picker)).perform(dragViewFromBottomToTop());

        // Choose the number of repetitions
        onView(withId(R.id.repetitions_picker)).perform(dragViewFromTopToBottom());

        // Click on the start circle
        onView(withId(R.id.effects_timeline)).perform(clickOnStartCircle());

        // Move the color pointer from right to bottom
        onView(withId(R.id.picker)).perform(dragColorWheelPointerFromRightToBottom());

        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        // Start the screen of your activity.
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_scan));

        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        // Start the screen of your activity.
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_design));

        // Check if save button is enabled
        onView(withId(R.id.save_preset_button)).check(matches(isEnabled()));

        // Check if preset button is enabled
        onView(withId(R.id.preview_preset_button)).check(matches(isEnabled()));

        // Check if blink button is in focus
        onView(withId(R.id.blink_button)).check(matches(isSelected()));
    }

    private ViewAction clickOnStopCircle(){

        CoordinatesProvider stopCircleCoordinatesProvider = new CoordinatesProvider() {
            @Override
            public float[] calculateCoordinates(View view) {
                int viewWidth;
                int viewHeight;

                int[] viewCoordinates = new int[2];

                view.getLocationOnScreen(viewCoordinates);
                viewWidth = view.getWidth();
                viewHeight = view.getHeight();

                viewCoordinates[0] = viewCoordinates[0] + (viewWidth - (viewWidth / 3));
                viewCoordinates[1] = viewCoordinates[1] + (viewHeight / 2);


                return new float[]{
                        (float) viewCoordinates[0],
                        (float) viewCoordinates[1]
                };
            }
        };

        return new GeneralClickAction(Tap.SINGLE, stopCircleCoordinatesProvider, Press.FINGER);
    }

    private ViewAction clickOnStartCircle(){

        CoordinatesProvider startCircleCoordinatesProvider = new CoordinatesProvider() {
            @Override
            public float[] calculateCoordinates(View view) {
                int viewWidth;
                int viewHeight;

                int[] viewCoordinates = new int[2];

                view.getLocationOnScreen(viewCoordinates);
                viewWidth = view.getWidth();
                viewHeight = view.getHeight();

                viewCoordinates[0] = viewCoordinates[0] + (viewWidth / 3);
                viewCoordinates[1] = viewCoordinates[1] + (viewHeight / 2);


                return new float[]{
                        (float) viewCoordinates[0],
                        (float) viewCoordinates[1]
                };
            }
        };

        return new GeneralClickAction(Tap.SINGLE, startCircleCoordinatesProvider, Press.FINGER);
    }

    private ViewAction dragViewFromTopToBottom(){
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.TOP_CENTER,
                GeneralLocation.BOTTOM_CENTER, Press.FINGER);
    }

    private ViewAction dragViewFromBottomToTop(){
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.BOTTOM_CENTER,
                GeneralLocation.TOP_CENTER, Press.FINGER);
    }


    private ViewAction dragColorWheelPointerFromTopToRight(){
        CoordinatesProvider startCoordinatesProvider = new CoordinatesProvider() {
            @Override
            public float[] calculateCoordinates(View view) {
                int viewWidth;

                int[] viewCoordinates = new int[2];

                view.getLocationOnScreen(viewCoordinates);
                viewWidth = view.getWidth();

                viewCoordinates[0] = viewCoordinates[0] + (viewWidth / 2);
                viewCoordinates[1] = viewCoordinates[1] + view.getPaddingTop() + 50;


                return new float[]{
                        (float) viewCoordinates[0],
                        (float) viewCoordinates[1]
                };
            }
        };

        CoordinatesProvider endCoordinatesProvider = new CoordinatesProvider() {
            @Override
            public float[] calculateCoordinates(View view) {
                int viewWidth;
                int viewHeight;

                int[] viewCoordinates = new int[2];

                view.getLocationOnScreen(viewCoordinates);
                viewWidth = view.getWidth();
                viewHeight = view.getHeight();

                viewCoordinates[0] = viewCoordinates[0] + viewWidth;
                viewCoordinates[1] = viewCoordinates[1] + (viewHeight / 2);

                return new float[]{
                        (float) viewCoordinates[0],
                        (float) viewCoordinates[1]
                };
            }
        };


        return new GeneralSwipeAction(Swipe.SLOW,
                startCoordinatesProvider,
                endCoordinatesProvider,
                Press.THUMB
        );
    }

    private ViewAction dragColorWheelPointerFromRightToBottom(){
        CoordinatesProvider startCoordinatesProvider = new CoordinatesProvider() {
            @Override
            public float[] calculateCoordinates(View view) {
                int viewWidth;
                int viewHeight;

                int[] viewCoordinates = new int[2];

                view.getLocationOnScreen(viewCoordinates);
                viewWidth = view.getWidth();
                viewHeight = view.getHeight();

                viewCoordinates[0] = viewCoordinates[0] + (viewWidth - 50);
                viewCoordinates[1] = viewCoordinates[1] + (viewHeight / 2);


                return new float[]{
                        (float) viewCoordinates[0],
                        (float) viewCoordinates[1]
                };
            }
        };

        CoordinatesProvider endCoordinatesProvider = new CoordinatesProvider() {
            @Override
            public float[] calculateCoordinates(View view) {
                int viewWidth;
                int viewHeight;

                int[] viewCoordinates = new int[2];

                view.getLocationOnScreen(viewCoordinates);
                viewWidth = view.getWidth();
                viewHeight = view.getHeight();

                viewCoordinates[0] = viewCoordinates[0] + (viewWidth / 2);
                viewCoordinates[1] = viewCoordinates[1] + viewHeight;

                return new float[]{
                        (float) viewCoordinates[0],
                        (float) viewCoordinates[1]
                };
            }
        };


        return new GeneralSwipeAction(Swipe.SLOW,
                startCoordinatesProvider,
                endCoordinatesProvider,
                Press.THUMB
        );
    }
}
