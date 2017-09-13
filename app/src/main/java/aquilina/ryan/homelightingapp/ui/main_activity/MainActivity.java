package aquilina.ryan.homelightingapp.ui.main_activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.ui.design_mode.DesignActivity;
import aquilina.ryan.homelightingapp.ui.presets_mode.PresetsActivity;
import aquilina.ryan.homelightingapp.ui.scan_mode.ScanActivity;

public class MainActivity extends AppCompatActivity {

    protected LinearLayout fullLayout;
    protected FrameLayout actContent;
    protected NavigationView mNavigationView;

    protected Typeface mHeaderTypeFace;
    protected Typeface mTextTypeFace;
    protected Typeface mSubTextTypeFace;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        // base layout
        fullLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_main, null);
        actContent = (FrameLayout) fullLayout.findViewById(R.id.activity_content);

        // Setting the content of layout that is provided through layoutResID
        getLayoutInflater().inflate(layoutResID, actContent, true);
        super.setContentView(fullLayout);

        AssetManager am = getAssets();
        mHeaderTypeFace = Typeface.createFromAsset(am, "fonts/Raleway-Bold.ttf");
        mTextTypeFace = Typeface.createFromAsset(am, "fonts/TitilliumWeb-Regular.ttf");
        mSubTextTypeFace = Typeface.createFromAsset(am, "fonts/TitilliumWeb-Italic.ttf");
        TextView title = (TextView) findViewById(R.id.appBarTitle);
        title.setTypeface(mHeaderTypeFace);

        Toolbar toolbar = (Toolbar) fullLayout.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) fullLayout.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) fullLayout.findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();
                Intent intent;

                if (id == R.id.nav_design) {
                    if(!mNavigationView.getMenu().findItem(R.id.nav_design).isChecked()){
                        intent = new Intent(getApplicationContext(), DesignActivity.class);
                        startActivity(intent);
                    }
                } else if (id == R.id.nav_lighting_mode) {
                    if(!mNavigationView.getMenu().findItem(R.id.nav_lighting_mode).isChecked()){
                        intent = new Intent(getApplicationContext(), PresetsActivity.class);
                        startActivity(intent);
                    }
                } else if (id == R.id.nav_scan) {
                    if(!mNavigationView.getMenu().findItem(R.id.nav_scan).isChecked()){
                        intent = new Intent(getApplicationContext(), ScanActivity.class);
                        startActivity(intent);
                    }
                }
                item.setChecked(true);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        changeMenuTypeFace(mNavigationView.getMenu());
    }

    /**
     * Show which item is selected in drawer
     * @param itemID
     */
    protected void setSelectedNavMenuItem(int itemID){
        for (int i = 0; i < mNavigationView.getMenu().size(); i++){
            mNavigationView.getMenu().getItem(i).setChecked(false);
        }

        mNavigationView.getMenu().findItem(itemID).setChecked(true);
    }

    /**
     * Change Typeface of menu
     */
    private void changeMenuTypeFace(Menu menu){
        for (int i = 0;i < menu.size();i++) {
            MenuItem mi = menu.getItem(i);

            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }
    }

    /**
     * Apply typeface to a menu item
     * @param mi
     */
    private void applyFontToMenuItem(MenuItem mi) {
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , mTextTypeFace), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
