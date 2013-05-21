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

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;
import group.pals.android.lib.ui.lockpattern.prefs.Prefs;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class PrefsActivity extends PreferenceActivity implements
        PreferenceHolder {

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            /*
             * Since this demo is a small app, we borrow ALP's preferences file.
             * If you're building a medium or large app, you should use your own
             * preference file. You can easily write some wrappers to forward
             * your preferences to ALP's preferences.
             */
            Prefs.setupPreferenceManager(this, getPreferenceManager());

            addPreferencesFromResource(R.xml.main_preferences);
            CommandsPrefsHelper.init(this, this);

            getListView().setCacheColorHint(
                    getResources().getColor(android.R.color.transparent));
        }
    }// onCreate()

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case CommandsPrefsHelper.REQ_CREATE_PATTERN: {
            if (resultCode == RESULT_OK)
                setTitle(new String(
                        data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN)));
            else
                setTitle(R.string.app_name);

            break;
        }// REQ_CREATE_PATTERN

        case CommandsPrefsHelper.REQ_ENTER_PATTERN: {
            int msgId = 0;

            switch (resultCode) {
            case RESULT_OK:
                msgId = android.R.string.ok;
                break;
            case RESULT_CANCELED:
                msgId = android.R.string.cancel;
                break;
            case LockPatternActivity.RESULT_FAILED:
                msgId = R.string.failed;
                break;
            default:
                return;
            }

            String msg = String.format("%s (%,d tries)", getString(msgId),
                    data.getIntExtra(LockPatternActivity.EXTRA_RETRY_COUNT, 0));

            Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
            toast.show();

            break;
        }// REQ_ENTER_PATTERN
        }
    }// onActivityResult()
}
