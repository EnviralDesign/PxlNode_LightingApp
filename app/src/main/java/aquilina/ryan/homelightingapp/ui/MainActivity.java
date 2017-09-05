package aquilina.ryan.homelightingapp.ui;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.ui.design_mode.DesignActivity;
import aquilina.ryan.homelightingapp.ui.presets_mode.PresetsActivity;
import aquilina.ryan.homelightingapp.ui.scan_mode.ScanActivity;

public class MainActivity extends AppCompatActivity {

    protected LinearLayout fullLayout;
    protected FrameLayout actContent;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        // base layout
        fullLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_main, null);
        actContent = (FrameLayout) fullLayout.findViewById(R.id.activity_content);

        // Setting the content of layout that is provided through layoutResID
        getLayoutInflater().inflate(layoutResID, actContent, true);
        super.setContentView(fullLayout);

        Toolbar toolbar = (Toolbar) fullLayout.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) fullLayout.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) fullLayout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();
                Intent intent;
                if (id == R.id.nav_design) {
                    intent = new Intent(getApplicationContext(), DesignActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_lighting_mode) {
                    intent = new Intent(getApplicationContext(), PresetsActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_scan) {
                    intent = new Intent(getApplicationContext(), ScanActivity.class);
                    startActivity(intent);
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
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
