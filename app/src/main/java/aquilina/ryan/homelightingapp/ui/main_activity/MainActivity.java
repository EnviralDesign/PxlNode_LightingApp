package aquilina.ryan.homelightingapp.ui.main_activity;

import com.google.gson.Gson;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.crypto.Mac;

import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.ui.design_mode.DesignActivity;
import aquilina.ryan.homelightingapp.ui.design_mode.DesignConfiguration;
import aquilina.ryan.homelightingapp.ui.group_managment.GroupManagementActivity;
import aquilina.ryan.homelightingapp.ui.group_managment.MacroManagementActivity;
import aquilina.ryan.homelightingapp.ui.group_managment.PresetManagementActivity;
import aquilina.ryan.homelightingapp.ui.lighting_mode.LightingModeActivity;
import aquilina.ryan.homelightingapp.ui.scan_mode.ScanActivity;
import aquilina.ryan.homelightingapp.utils.Constants;

public class MainActivity extends AppCompatActivity {

    protected DrawerLayout fullLayout;
    protected FrameLayout actContent;
    protected NavigationView mNavigationView;

    protected Typeface mHeaderTypeFace;
    protected Typeface mTextTypeFace;
    protected Typeface mSubTextTypeFace;

    protected ActionBarDrawerToggle mToggle;
    protected Toolbar mToolbar;
    protected DrawerLayout mDrawer;

    private boolean mToolBarNavigationListenerIsRegistered = false;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        // base layout
        final ViewGroup nullParent = null;
        fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_main, nullParent);
        actContent = fullLayout.findViewById(R.id.activity_content);

        // Setting the content of layout that is provided through layoutResID
        getLayoutInflater().inflate(layoutResID, actContent, true);
        super.setContentView(fullLayout);

        AssetManager am = getAssets();
        mHeaderTypeFace = Typeface.createFromAsset(am, "fonts/raleway_bold.ttf");
        mTextTypeFace = Typeface.createFromAsset(am, "fonts/titilliumweb_regular.ttf");
        mSubTextTypeFace = Typeface.createFromAsset(am, "fonts/titilliumweb_italic.ttf");
        TextView title = findViewById(R.id.appBarTitle);
        title.setTypeface(mHeaderTypeFace);

        mToolbar = fullLayout.findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        mDrawer = fullLayout.findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(mToggle);
        mToggle.syncState();

        mNavigationView = fullLayout.findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();
                Intent intent;

                if (id == R.id.nav_design) {
                    if(!mNavigationView.getMenu().findItem(R.id.nav_design).isChecked()){
                        intent = new Intent(getApplicationContext(), DesignActivity.class);
                        Bundle bundle = loadDesignConfigurationVariables();
                        if(bundle != null){
                            intent.putExtras(bundle);
                        }
                        startActivity(intent);
                    }
                } else if (id == R.id.nav_lighting_mode) {
                    if(!mNavigationView.getMenu().findItem(R.id.nav_lighting_mode).isChecked()){
                        intent = new Intent(getApplicationContext(), LightingModeActivity.class);
                        startActivity(intent);
                    }
                } else if (id == R.id.nav_scan) {
                    if(!mNavigationView.getMenu().findItem(R.id.nav_scan).isChecked()){
                        intent = new Intent(getApplicationContext(), ScanActivity.class);
                        startActivity(intent);
                    }
                } else if (id == R.id.nav_group_node_groups) {
                    if (!mNavigationView.getMenu().findItem(R.id.nav_group_node_groups).isChecked()) {
                        intent = new Intent(getApplicationContext(), GroupManagementActivity.class);
                        startActivity(intent);
                    }
                } else if(id == R.id.nav_group_presets){
                    if (!mNavigationView.getMenu().findItem(R.id.nav_group_presets).isChecked()) {
                        intent = new Intent(getApplicationContext(),PresetManagementActivity.class);
                        startActivity(intent);
                    }
                } else if(id == R.id.nav_group_macros){
                    if (!mNavigationView.getMenu().findItem(R.id.nav_group_macros).isChecked()) {
                        intent = new Intent(getApplicationContext(), MacroManagementActivity.class);
                        startActivity(intent);
                    }
                }

                item.setChecked(true);
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        changeMenuTypeFace(mNavigationView.getMenu());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Show which item is selected in drawer
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
     * Loads the configuration variables if found.
     */
    private Bundle loadDesignConfigurationVariables(){
        SharedPreferences Prefs = getSharedPreferences(Constants.DESIGN_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String json = Prefs.getString(Constants.DESIGN_CONFIGURATION, null);

        Gson gson = new Gson();
        DesignConfiguration designConfiguration = gson.fromJson(json, DesignConfiguration.class);

        if(designConfiguration != null){
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.DESIGN_START_COLOR, designConfiguration.getStartColor());
            bundle.putInt(Constants.DESIGN_STOP_COLOR, designConfiguration.getEndColor());
            bundle.putInt(Constants.DESIGN_CENTER_COLOR, designConfiguration.getCentreCircleColor());
            bundle.putInt(Constants.DESIGN_REPETITION, designConfiguration.getRepetitions());
            bundle.putInt(Constants.DESIGN_DURATION, designConfiguration.getDuration());
            bundle.putString(Constants.DESIGN_CURRENT_EFFECT, designConfiguration.getEffect());
            bundle.putString(Constants.DESIGN_CURRENT_COMMAND, designConfiguration.getCommand());
            bundle.putInt(Constants.DESIGN_CURRENT_SPINNER_POSITION, designConfiguration.getSpinnerPosition());
            bundle.putIntegerArrayList(Constants.DESIGN_SELECTED_DEVICES, designConfiguration.getSelectedDevices());

            return bundle;
        }

        return null;
    }

    /**
     * Apply typeface to a menu item
     */
    private void applyFontToMenuItem(MenuItem mi) {
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , mTextTypeFace), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    protected void enableBackButton(boolean enable){
        if(enable){
            // remove hamburger
            mToggle.setDrawerIndicatorEnabled(false);

            // Show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            if(!mToolBarNavigationListenerIsRegistered){
                mToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onBackPressed();
                    }
                });

                mToolBarNavigationListenerIsRegistered = true;
            }
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            mToggle.setDrawerIndicatorEnabled(true);
            mToggle.setToolbarNavigationClickListener(null);
            mToolBarNavigationListenerIsRegistered = false;
        }
    }
}
