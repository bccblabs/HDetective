package fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by bski on 9/28/15.
 */
public class SettingsFragment extends PreferenceFragment {
    public static SettingsFragment newInstance () {
        SettingsFragment settingsFragment = new SettingsFragment();
        return settingsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
