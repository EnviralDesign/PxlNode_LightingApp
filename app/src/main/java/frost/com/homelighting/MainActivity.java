package frost.com.homelighting;

import com.google.gson.Gson;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import frost.com.homelighting.db.entity.DeviceEntity;
import frost.com.homelighting.db.entity.OnlineDeviceEntity;
import frost.com.homelighting.ui.about.AboutFragment;
import frost.com.homelighting.ui.designmode.ErrorFragment;
import frost.com.homelighting.ui.macros.MacroFragment;
import frost.com.homelighting.ui.presets.PresetFragment;
import frost.com.homelighting.ui.configuration.ConfigurationFragment;
import frost.com.homelighting.ui.designmode.DesignConfiguration;
import frost.com.homelighting.ui.designmode.DesignFragment;
import frost.com.homelighting.ui.groups.GroupFragment;
import frost.com.homelighting.ui.lighting.LightingFragment;
import frost.com.homelighting.ui.scan.ScanFragment;
import frost.com.homelighting.viewmodel.MainViewModel;

import static frost.com.homelighting.util.Constants.DESIGN_CENTER_COLOR;
import static frost.com.homelighting.util.Constants.DESIGN_CONFIGURATION;
import static frost.com.homelighting.util.Constants.DESIGN_CURRENT_COMMAND;
import static frost.com.homelighting.util.Constants.DESIGN_CURRENT_EFFECT;
import static frost.com.homelighting.util.Constants.DESIGN_CURRENT_SPINNER_POSITION;
import static frost.com.homelighting.util.Constants.DESIGN_DURATION;
import static frost.com.homelighting.util.Constants.DESIGN_REPETITION;
import static frost.com.homelighting.util.Constants.DESIGN_SELECTED_DEVICES;
import static frost.com.homelighting.util.Constants.DESIGN_SHARED_PREFERENCES;
import static frost.com.homelighting.util.Constants.DESIGN_SPRITE_SELECTION;
import static frost.com.homelighting.util.Constants.DESIGN_START_CIRCLE_STATE;
import static frost.com.homelighting.util.Constants.DESIGN_START_COLOR;
import static frost.com.homelighting.util.Constants.DESIGN_STOP_COLOR;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACTIVITY";

    private List<DeviceEntity> mScannedDevicesList;

    private NavigationView mNavigationView;
    protected ActionBarDrawerToggle mToggle;
    protected Toolbar mToolbar;
    protected DrawerLayout mDrawer;
    private OnBackClickListener onBackClickListener;
    public List<DeviceEntity> mOnlineDevices;
    public FragmentManager mFragmentManager;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    MainViewModel mainViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((HomeLightingApplication) getApplication())
                .getApplicationComponent()
                .inject(this);

        mainViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MainViewModel.class);

        mOnlineDevices = new ArrayList<>();
        mScannedDevicesList = new ArrayList<>();

        mainViewModel.getOnlineDevices().observe(this, new Observer<List<DeviceEntity>>() {
            @Override
            public void onChanged(@Nullable List<DeviceEntity> deviceEntities) {
                mOnlineDevices = deviceEntities;
            }
        });

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDrawer = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mToggle.setDrawerIndicatorEnabled(false);
        mToggle.setHomeAsUpIndicator(getDrawable(R.drawable.burger_icon));
        mToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(GravityCompat.START);
            }
        });
        mDrawer.setDrawerListener(mToggle);
        mToggle.syncState();

        mFragmentManager = getSupportFragmentManager();

        addFragmentToActivity(ScanFragment.newInstance());

        mNavigationView = findViewById(R.id.nav_view);
        disableNavigationViewScrollbars(mNavigationView);

        mNavigationView.getMenu().findItem(R.id.nav_scan).setChecked(true);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();
                if (id == R.id.nav_design) {
                    if(mOnlineDevices.isEmpty()){
                        addFragmentToActivity(ErrorFragment.newInstance());
                    } else{
                        Bundle bundle = loadDesignConfigurationVariables();
                        addFragmentToActivity(DesignFragment.newInstance(bundle));
                    }
                } else if (id == R.id.nav_lighting_mode) {
                    addFragmentToActivity(LightingFragment.newInstance());
                } else if(id == R.id.nav_configuration){
                    addFragmentToActivity(ConfigurationFragment.newInstance());
                } else if (id == R.id.nav_scan) {
                    addFragmentToActivity(ScanFragment.newInstance());
                } else if (id == R.id.nav_group_node_groups) {
                    addFragmentToActivity(GroupFragment.newInstance());
                } else if(id == R.id.nav_group_presets){
                    addFragmentToActivity(PresetFragment.newInstance());
                } else if(id == R.id.nav_group_macros){
                    addFragmentToActivity(MacroFragment.newInstance());
                } else if(id == R.id.nav_about){
                    addFragmentToActivity(AboutFragment.newInstance());
                }
                mDrawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (onBackClickListener != null && onBackClickListener.onBackClick()) {
            return;
        }
        super.onBackPressed();
    }

    public interface OnBackClickListener {
        boolean onBackClick();
    }

    public void setOnBackClickListener(OnBackClickListener onBackClickListener) {
        this.onBackClickListener = onBackClickListener;
    }

    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    private void addFragmentToActivity (Fragment fragment) {
        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_content, fragment, TAG)
                .commit();
    }

    public void setTitle(int id){
        setTitle(getString(id));
    }

    public void setTitle(String title){
        TextView textView = findViewById(R.id.appBarTitle);
        textView.setText(title);
    }

    public void showToast(int stringId){
        showToast(getString(stringId));
    }
    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Loads the configuration variables if found.
     */
    private Bundle loadDesignConfigurationVariables(){
        SharedPreferences Prefs = getSharedPreferences(DESIGN_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String json = Prefs.getString(DESIGN_CONFIGURATION, null);

        Gson gson = new Gson();
        DesignConfiguration designConfiguration = gson.fromJson(json, DesignConfiguration.class);

        if(designConfiguration != null){
            Bundle bundle = new Bundle();
            bundle.putInt(DESIGN_START_COLOR, designConfiguration.getStartColor());
            bundle.putInt(DESIGN_STOP_COLOR, designConfiguration.getEndColor());
            bundle.putInt(DESIGN_CENTER_COLOR, designConfiguration.getCentreCircleColor());
            bundle.putInt(DESIGN_REPETITION, designConfiguration.getRepetitions());
            bundle.putInt(DESIGN_DURATION, designConfiguration.getDuration());
            bundle.putString(DESIGN_CURRENT_EFFECT, designConfiguration.getEffect());
            bundle.putString(DESIGN_CURRENT_COMMAND, designConfiguration.getCommand());
            bundle.putInt(DESIGN_CURRENT_SPINNER_POSITION, designConfiguration.getSpinnerPosition());
            bundle.putStringArrayList(DESIGN_SELECTED_DEVICES, designConfiguration.getSelectedDevicesIP());
            bundle.putBoolean(DESIGN_START_CIRCLE_STATE, designConfiguration.isStartCircleState());
            bundle.putInt(DESIGN_SPRITE_SELECTION, designConfiguration.getSpritesSpinnerPosition());
            return bundle;
        }

        return null;
    }
}
