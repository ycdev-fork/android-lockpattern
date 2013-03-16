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
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int _ReqCreateLockPattern = 0;
    private static final int _ReqEnterLockPattern = 1;

    private CheckBox mChkDialog;
    private CheckBox mChkStealthMode;
    private Button mBtnCreateLockPattern;
    private Button mBtnEnterLockPattern;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        mChkDialog = (CheckBox) findViewById(R.id.dialog);
        mChkStealthMode = (CheckBox) findViewById(R.id.stealth_mode);
        mBtnCreateLockPattern = (Button) findViewById(R.id.create_lockpattern);
        mBtnEnterLockPattern = (Button) findViewById(R.id.enter_lockpattern);

        mBtnCreateLockPattern
                .setOnClickListener(mBtnCreateLockPatternOnClickListener);
        mBtnEnterLockPattern
                .setOnClickListener(mBtnEnterLockPatternOnClickListener);
    }// onCreate()

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case _ReqCreateLockPattern:
            if (resultCode == RESULT_OK)
                setTitle(data.getStringExtra(LockPatternActivity._Pattern));
            else
                setTitle(R.string.app_name);

            break;// _ReqCreateLockPattern

        case _ReqEnterLockPattern:
            Toast toast = Toast.makeText(this,
                    resultCode == RESULT_OK ? android.R.string.ok
                            : android.R.string.cancel, Toast.LENGTH_SHORT);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            break;// _ReqEnterLockPattern
        }
    }// onActivityResult()

    private final View.OnClickListener mBtnCreateLockPatternOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(LockPatternActivity._ActionCreatePattern,
                    null, MainActivity.this, LockPatternActivity.class);
            i.putExtra(LockPatternActivity._Theme,
                    mChkDialog.isChecked() ? R.style.Alp_Theme_Dialog_Dark
                            : R.style.Alp_Theme_Dark);
            i.putExtra(LockPatternActivity._StealthMode,
                    mChkStealthMode.isChecked());
            i.putExtra(LockPatternActivity._EncrypterClass, LPEncrypter.class);
            i.putExtra(LockPatternActivity._AutoSave, true);
            startActivityForResult(i, _ReqCreateLockPattern);
        }
    };// mBtnCreateLockPatternOnClickListener

    private final View.OnClickListener mBtnEnterLockPatternOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(LockPatternActivity._ActionComparePattern,
                    null, MainActivity.this, LockPatternActivity.class);
            i.putExtra(LockPatternActivity._Theme,
                    mChkDialog.isChecked() ? R.style.Alp_Theme_Dialog_Dark
                            : R.style.Alp_Theme_Dark);
            i.putExtra(LockPatternActivity._StealthMode,
                    mChkStealthMode.isChecked());
            i.putExtra(LockPatternActivity._EncrypterClass, LPEncrypter.class);
            i.putExtra(LockPatternActivity._AutoSave, true);
            startActivityForResult(i, _ReqEnterLockPattern);
        }
    };// mBtnEnterLockPatternOnClickListener
}
