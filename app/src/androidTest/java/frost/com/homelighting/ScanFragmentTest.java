package frost.com.homelighting;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import android.view.Gravity;


@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScanFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class, true, true);


    @Before
    public void navigateToScanFragment(){

        // Open drawer
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        // Select scan fragment
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_scan));

        //Wait until drawer is closed
        try{
            Thread.sleep(500);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void A_scanForDevices_CheckDevicesAreLoaded(){
        onView(withId(R.id.refresh))
                .perform(click());

        //Progress dialogue is showing
        try{
            Thread.sleep(6000);
        } catch (Exception e){
            e.printStackTrace();
        }

        // Check if a device is scanned
        onView(withId(R.id.no_devices_found)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void B_clickOnItemsAddToGroup_checkGroup(){
        // Click on the first device
        onView(withId(R.id.devices_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

        onView(withId(R.id.add_to_group_fab))
                .perform(click());

        onView(withText("Save Group")).check(matches(isDisplayed()));
    }

    @Test
    public void C_clickOnSaveGroupButton_checkGroupIsSaved(){

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

        // Open drawer
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        // Select Groups fragment
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_group_node_groups));

        // Check if group has been saved
        onView(withText("Test Group")).check(matches(isDisplayed()));
    }

    @Test
    public void D_rejectNullGroupName_checkDialogIsStillOpen(){
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

        // Check if Dialog is still open
        onView(withText("Save Group")).check(matches(isDisplayed()));
    }
}
