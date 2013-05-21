/*
 *   Copyright 2012 Hai Bison
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package group.pals.android.lib.ui.lockpattern.demo;

import group.pals.android.lib.ui.lockpattern.prefs.Prefs;

import java.util.List;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Preferences activity for API 11+.
 * 
 * @author Hai Bison
 * 
 */
public class PrefsActivity_v11 extends PrefsActivity {

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.main_preferences_headers, target);
    }// onBuildHeaders()

    /**
     * This fragment shows the preferences for SETTINGS.
     */
    public static class Fragment_Prefs_Settings extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            /*
             * Since this demo is a small app, we borrow ALP's preferences file.
             * If you're building a medium or large app, you should use your own
             * preference file. You can easily write some wrappers to forward
             * your preferences to ALP's preferences.
             */
            Prefs.setupPreferenceManager(getActivity(), getPreferenceManager());

            addPreferencesFromResource(R.xml.main_preferences_settings);
        }// onCreate()
    }// Fragment_Prefs_Settings

    /**
     * This fragment shows the COMMANDS.
     */
    public static class Fragment_Prefs_Commands extends PreferenceFragment
            implements PreferenceHolder {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.main_preferences_commands);
            CommandsPrefsHelper.init(getActivity(), this);
        }// onCreate()
    }// Fragment_Prefs_Commands
}
