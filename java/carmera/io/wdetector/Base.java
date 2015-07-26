package carmera.io.wdetector;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcelable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.ParcelableSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import fragments.CaptureFragment;
import fragments.CreateReportFragment;
import fragments.EditSaveResultsFragment;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;


public class Base extends ActionBarActivity implements ViewAnimator.ViewAnimatorListener,
                                                       EditSaveResultsFragment.OnRetakePhotoCallback,
                                                       CaptureFragment.OnCameraResultListener {

    public static final String CLOSE = "Close";
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private List<SlideMenuItem> list = new ArrayList<>();
    private CaptureFragment captureFragmentFragment;
    private EditSaveResultsFragment editSaveResultsFragment;
    private ViewAnimator viewAnimator;
    private LinearLayout linearLayout;

    private String TAG = this.getClass().getCanonicalName();

    @Override
    public void retakePhoto() {
        captureFragmentFragment = CaptureFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, captureFragmentFragment)
                .commit();

    }

    @Override
    public void OnCameraResult (Parcelable image_data) {
        editSaveResultsFragment = new EditSaveResultsFragment();
        assert (image_data != null);
        Bundle args = new Bundle();
        args.putParcelable(EditSaveResultsFragment.EXTRA_IMAGE_DATA , image_data);
        editSaveResultsFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, editSaveResultsFragment)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base);
        captureFragmentFragment = CaptureFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, captureFragmentFragment)
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
        viewAnimator = new ViewAnimator<>(this, list, captureFragmentFragment, drawerLayout, this);
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
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
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

            /** Called when a drawer has settled in a completely open state. */
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
//        View view = findViewById(R.id.content_frame);
//        int finalRadius = Math.max(view.getWidth(), view.getHeight());
//        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(view, 0, topPosition, 0, finalRadius);
//        animator.setInterpolator(new AccelerateInterpolator());
//        animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);
//        findViewById(R.id.content_overlay).setBackgroundDrawable(new BitmapDrawable(getResources(), screenShotable.getBitmap()));
//        animator.start();

        switch (fragmentName) {
            case "Capture":
                captureFragmentFragment = CaptureFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, captureFragmentFragment).commit();
                return captureFragmentFragment;
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
        getSupportActionBar().setHomeButtonEnabled(false);

    }

    @Override
    public void enableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerLayout.closeDrawers();

    }

    @Override
    public void addViewToContainer(View view) {
        linearLayout.addView(view);
    }
}