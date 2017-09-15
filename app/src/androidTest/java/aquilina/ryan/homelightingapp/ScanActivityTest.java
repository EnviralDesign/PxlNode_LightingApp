package aquilina.ryan.homelightingapp;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import android.support.test.espresso.UiController;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.espresso.Espresso;
import android.support.v7.widget.RecyclerView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import aquilina.ryan.homelightingapp.ui.scan_mode.ScanActivity;

/**
 * Created by SterlingRyan on 9/15/2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ScanActivityTest {
    @Rule
    public ActivityTestRule<ScanActivity> mActivityRule = new ActivityTestRule<>(
            ScanActivity.class
    );

    @Test
    public void scrollItemsDown_checkItemsLoad(){
        // Scroll the recycler view down
        Espresso.onView(ViewMatchers.withId(R.id.devices_list_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(14, ViewActions.click()));

        String deviceName = "Light Name";
        onView(withId(R.id.devices_list_view)).check(matches(hasDescendant(withText(deviceName))));
    }

    @Test
    public void clickOnItemsAddToGroup_checkGroup(){
        onView(withId(R.id.devices_list_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(3, ViewActions.click()))
                .perform(RecyclerViewActions.actionOnItemAtPosition(6, ViewActions.click()))
                .perform(RecyclerViewActions.actionOnItemAtPosition(9, ViewActions.click()))
                .perform(RecyclerViewActions.actionOnItemAtPosition(12, ViewActions.click()));

        onView(withId(R.id.add_to_group_fab))
                .perform(click());

        onView(withText("Save Group")).check(matches(isDisplayed()));
    }
}
