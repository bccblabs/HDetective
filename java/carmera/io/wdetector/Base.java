package carmera.io.wdetector;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import fragments.CaptureFragment;
import fragments.InitFragment;
import fragments.CreateReportFragment;
import fragments.EditSaveResultsFragment;
import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;


public class Base extends ActionBarActivity implements ViewAnimator.ViewAnimatorListener,
                                                       EditSaveResultsFragment.OnRetakePhotoCallback,
                                                       CaptureFragment.OnCameraResultListener,
                                                       InitFragment.StartCaptureListener {

    public static final String CLOSE = "Close";
    public static final String EXTRA_SAMPLE_DETAILS = "extra_sample_details";
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private List<SlideMenuItem> list = new ArrayList<>();

    private InitFragment initFragment;
    private EditSaveResultsFragment editSaveResultsFragment;
    private CaptureFragment captureFragment;
    private ViewAnimator viewAnimator;
    private LinearLayout linearLayout;

    private String TAG = this.getClass().getCanonicalName();

    @Override
    public void retakePhoto() {
        initFragment = InitFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, initFragment)
                .commit();

    }

    @Override
    public void OnStartCapture(Parcelable hdSample) {
        captureFragment = CaptureFragment.newInstance();
        assert (hdSample != null);
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_SAMPLE_DETAILS, hdSample);
        captureFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, captureFragment)
                .commit();
    }

    @Override
    public void OnCameraResult (Parcelable hdSample) {
        editSaveResultsFragment = new EditSaveResultsFragment();
        assert (hdSample != null);
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_SAMPLE_DETAILS, hdSample);
        editSaveResultsFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, editSaveResultsFragment)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base);
        initFragment = InitFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, initFragment)
                .commit();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        linearLayout = (LinearLayout) findViewById(R.id.left_drawer);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });


        setActionBar();
        createMenuList();
        viewAnimator = new ViewAnimator<>(this, list, initFragment, drawerLayout, this);
    }

    private void createMenuList() {
        SlideMenuItem close = new SlideMenuItem(CLOSE, R.drawable.icn_close);
        list.add(close);
        SlideMenuItem menuItem0 = new SlideMenuItem("Examine", R.drawable.ic_action_camera_white_small);
        list.add(menuItem0);
        SlideMenuItem menuItem1 = new SlideMenuItem("History", R.drawable.ic_history_white_24dp);
        list.add(menuItem1);
    }

    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.background_floating_material_light));
        setSupportActionBar(toolbar);
        ActionBar actionbar  = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setHomeButtonEnabled(true);
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,R.string.drawer_open,R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                linearLayout.removeAllViews();
                linearLayout.invalidate();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset > 0.6 && linearLayout.getChildCount() == 0)
                    viewAnimator.showMenuContent();
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private ScreenShotable replaceFragment(ScreenShotable screenShotable, int topPosition, String fragmentName) {
        switch (fragmentName) {
            case "Capture":
                initFragment = InitFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, initFragment).commit();
                return initFragment;
            case "History":
                CreateReportFragment createReportFragment = new CreateReportFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, createReportFragment).commit();
                return createReportFragment;
        }
        return null;
    }


    @Override
    public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {
        switch (slideMenuItem.getName()) {
            case CLOSE:
                return screenShotable;
            case "Capture": {
                return replaceFragment(screenShotable, position, "Capture");
            }
            case "History": {
                return replaceFragment(screenShotable, position, "History");
            }
            default:
                return replaceFragment(screenShotable, position, "Capture");
        }
    }

    @Override
    public void disableHomeButton() {
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null)
            toolbar.setHomeButtonEnabled(false);
    }

    @Override
    public void enableHomeButton() {
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null)
            toolbar.setHomeButtonEnabled(true);
        drawerLayout.closeDrawers();
    }

    @Override
    public void addViewToContainer(View view) {
        linearLayout.addView(view);
    }
}