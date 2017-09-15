package aquilina.ryan.homelightingapp;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import aquilina.ryan.homelightingapp.model.DevicesGroup;
import aquilina.ryan.homelightingapp.ui.design_mode.DesignActivity;

/**
 * Created by SterlingRyan on 9/15/2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DesignActivityTest {

    @Rule
    public ActivityTestRule<DesignActivity> mActivityRule = new ActivityTestRule<>(
            DesignActivity.class
    );

    @Test
    public void selectGroupItem_checkEffectControls(){
        onView(withId(R.id.item_spinner))
                .perform(click());

        onData(allOf(is(instanceOf(Object.class)))).atPosition(2).perform(click());

        onView(withId(R.id.picker)).check(matches(isDisplayed()));
    }

    @Test
    public void selectEffectItem_checkEffectControls(){
        onView(withId(R.id.item_spinner))
                .perform(click());

        onData(allOf(is(instanceOf(Object.class)))).atPosition(2).perform(click());

        onView(withId(R.id.blink_button))
                .perform(click());

        onView(withId(R.id.effects_timeline)).check(matches(isDisplayed()));
    }

    @Test
    public void savePreset_checkDialog(){
        onView(withId(R.id.item_spinner))
                .perform(click());

        onData(allOf(is(instanceOf(Object.class)))).atPosition(2).perform(click());

        onView(withId(R.id.blink_button))
                .perform(click());

        onView(withId(R.id.save_preset_button)).perform(click());

        onView(withText("Save Preset")).check(matches(isDisplayed()));
    }
}
