package carmera.io.wdetector;

import android.os.Parcelable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.parse.ParseUser;
import com.yalantis.guillotine.animation.GuillotineAnimation;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import Util.Util;
import butterknife.Bind;
import butterknife.ButterKnife;
import fragments.CaptureFragment;
import fragments.ExamineResultsFragment;
import fragments.InitFragment;
import fragments.EditSaveResultsFragment;
import models.HDSample;


public class Base extends AppCompatActivity implements CaptureFragment.OnCameraResultListener,
                                                       InitFragment.StartCaptureListener {

    private final String TAG = getClass().getCanonicalName();
    public static final String EXTRA_SAMPLE_DETAILS = "extra_sample_details";
    private static final long RIPPLE_DURATION = 250;
    private static GuillotineAnimation guillotineAnimation;
    private View start_verification;
    private View check_results;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.root)
    FrameLayout root;
    @Bind(R.id.content_hamburger)
    View contentHamburger;

    @Override
    public void OnStartCapture(Parcelable hdSample) {
        CaptureFragment captureFragment = CaptureFragment.newInstance();
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
        try {
            Log.i(TAG, "Socket Emit Upload Event");
            HDSample hdd_sample = Parcels.unwrap(hdSample);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddhh:mm:ss.mmm", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            hdd_sample.setDate(sdf.format(new Date()));
            hdd_sample.setTesterId(ParseUser.getCurrentUser().getUsername());

            String hdd_json_string = new Gson().toJson(hdd_sample);
            Log.i(TAG, hdd_json_string);
            Util.getUploadSocket().emit("clz_data", hdd_json_string);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, InitFragment.newInstance())
                    .commit();
        } catch (Exception e) {
            Log.e(TAG, "Socket Emit Upload Event Error: " + e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base);
        ButterKnife.bind(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, InitFragment.newInstance())
                .commit();

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        root.addView(guillotineMenu);
        start_verification = guillotineMenu.findViewById(R.id.start_verification);
        check_results = guillotineMenu.findViewById(R.id.check_results);

        guillotineAnimation = new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .build();


        Log.i(TAG, "Socket Emit Connection Event");
        Util.getUploadSocket().connect();
        Util.getUploadSocket().on("register", OnRegister);

        start_verification.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, InitFragment.newInstance())
                        .commit();
                guillotineAnimation.close();
                return false;
            }
        });

        check_results.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, ExamineResultsFragment.newInstance())
                        .commit();
                guillotineAnimation.close();
                return false;
            }
        });
    }

    private Emitter.Listener OnRegister = new Emitter.Listener() {
        @Override

        public void call (final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = (String) args[0];
                    Log.i (TAG, "connection socket id: " + data);
                }
            });
        }
    };

    @Override
    public void onDestroy () {
        super.onDestroy();
        Log.i (TAG, "[socket] disconnects");
        Util.getUploadSocket().disconnect();
    }

}