package frost.com.homelighting;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;

import android.support.test.espresso.PerformException;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class F_GroupFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class, true, true);

    @Before
    public void navigateToDesignFragment(){

        // Open drawer
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        // Select design fragment
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_group_node_groups));

        //Wait until drawer is closed
        try{
            Thread.sleep(500);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void deleteGroup_checkListSize(){
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
                onView(withText(R.string.text_view_create_group)).check(matches(isDisplayed()));

            }
        } catch (PerformException e){
            e.printStackTrace();
            onView(withText(R.string.text_view_create_group)).check(matches(isDisplayed()));
        }
    }
}