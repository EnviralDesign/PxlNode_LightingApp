package frost.com.homelighting;

import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
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
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import frost.com.homelighting.db.entity.DeviceEntity;
import frost.com.homelighting.db.entity.GroupEntity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class C_LightingFragmentTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class, true, true);

    @Before
    public void navigateToLightingFragment(){

        // Open drawer
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        // Select design fragment
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_lighting_mode));

        //Wait until drawer is closed
        try{
            Thread.sleep(500);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void A_addPreset_checkItIsShown(){
        // Open drawer
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        // Select design fragment
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_design));

        //Wait until drawer is closed
        try{
            Thread.sleep(500);
        } catch (Exception e){
            e.printStackTrace();
        }

        // Click on the material spinner.
        onView(withId(R.id.item_spinner))
                .perform(click());

        // Click on a device.
        onData(allOf(is(instanceOf(GroupEntity.class)))).atPosition(0).perform(click());

        // Enable an effect.
        onView(withId(R.id.blink_button))
                .perform(click());

        // Move the color pointer
        onView(withId(R.id.picker)).perform(dragColorWheelPointerFromTopToRight());

        // Pull down to the bottom of the page
        onView(withId(R.id.parent_scroll_view)).perform(swipeDown());

        // Choose the duration amount
        onView(withId(R.id.duration_picker)).perform(dragViewFromBottomToTop());

        // Choose the number of repetitions
        onView(withId(R.id.repetitions_picker)).perform(dragViewFromTopToBottom());

        // Click on the start circle
        onView(withId(R.id.effects_timeline)).perform(clickOnStartCircle());

        // Move the color pointer from right to bottom
        onView(withId(R.id.picker)).perform(dragColorWheelPointerFromRightToBottom());

        // Click on save preset button
        onView(withId(R.id.save_preset_button)).check(matches(allOf(isEnabled(), isClickable()))).perform(clickIfAvailable);

        // Type the name of the preset
        onView(withId(R.id.dialog_edit_text)).perform(typeText("Test Lighting Preset"));

        // Click on save button
        onView(withId(R.id.button2)).perform(click());

        // Open drawer
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        // Select design fragment
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_lighting_mode));

        //Wait until drawer is closed
        try{
            Thread.sleep(500);
        } catch (Exception e){
            e.printStackTrace();
        }

        // Check if the preset is available
        onView(withText("Test Lighting Preset")).check(matches(isDisplayed()));
    }

    @Test
    public void B_addMacro_checkIfShown(){
        // Long click the first preset
        onView(withId(R.id.presets_recycler_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, longClick()));

        // Click on the add macro menu button
        onView(withId(R.id.add_macro_button)).perform(click());

        // Type the name of the macro
        onView(withId(R.id.dialog_edit_text)).perform(typeText("Test Lighting Macro"));

        // Click on save button
        onView(withId(R.id.button2)).perform(click());

        // Check if the macro is available
        onView(withText("Test Lighting Macro")).check(matches(isDisplayed()));
    }

    @Test
    public void C_deleteMacro_checkListSize(){
        try {
            RecyclerView.Adapter adapter = ((RecyclerView) mActivityRule.getActivity().getWindow().getDecorView().findViewById(R.id.presets_recycler_list)).getAdapter();
            int recyclerViewSize = adapter.getItemCount();

            // Long click the first preset
            onView(withId(R.id.presets_recycler_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, longClick()));

            // Click on the delete menu button
            onView(withId(R.id.delete_button)).perform(click());

            if (recyclerViewSize > 1) {

                // Check if list size has decreased by one
                onView(withId(R.id.presets_recycler_list)).check(new RecyclerViewItemCountAssertion(recyclerViewSize - 2));

            } else {

                // Check id error is shown
                onView(withText(R.string.text_view_create_preset_or_macro)).check(matches(isDisplayed()));

            }
        } catch (PerformException e){
            e.printStackTrace();
            onView(withText(R.string.text_view_create_preset_or_macro)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void D_deletePreset_checkListSize(){
        try {
            RecyclerView.Adapter adapter = ((RecyclerView) mActivityRule.getActivity().getWindow().getDecorView().findViewById(R.id.presets_recycler_list)).getAdapter();
            int recyclerViewSize = adapter.getItemCount();

            // Long click the first preset
            onView(withId(R.id.presets_recycler_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, longClick()));

            // Click on the delete menu button
            onView(withId(R.id.delete_button)).perform(click());

            if (recyclerViewSize > 1) {

                // Check if list size has decreased by one
                onView(withId(R.id.presets_recycler_list)).check(new RecyclerViewItemCountAssertion(recyclerViewSize - 2));

            } else {

                // Check id error is shown
                onView(withText(R.string.text_view_create_preset_or_macro)).check(matches(isDisplayed()));

            }
        } catch (PerformException e){
            e.printStackTrace();
            onView(withText(R.string.text_view_create_preset_or_macro)).check(matches(isDisplayed()));
        }
    }

    private ViewAction clickIfAvailable = new ViewAction() {
        @Override
        public Matcher<View> getConstraints() {
            return ViewMatchers.isEnabled(); // no constraints, they are checked above
        }

        @Override
        public String getDescription() {
            return "click plus button";
        }

        @Override
        public void perform(UiController uiController, View view) {
            view.performClick();
        }
    };

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
