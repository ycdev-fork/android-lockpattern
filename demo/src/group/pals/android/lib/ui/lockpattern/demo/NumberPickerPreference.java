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

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Number picker preference.
 * 
 * @author Hai Bison
 * 
 */
public class NumberPickerPreference extends DialogPreference {

    // private static final String CLASSNAME = NumberPickerPreference.class
    // .getName();

    /**
     * Default mValue of this preference.
     */
    public static final int DEFAULT_VALUE = 0;

    /*
     * CONTROLS
     */

    private TextView mTextNumber;
    private View mBtnIncrease;
    private View mBtnDecrease;

    /*
     * FIELDS
     */

    private final int mMin;
    private final int mMax;
    private final int mIncrement;
    private int mCurrentValue = 0;

    /**
     * Creates new instance.
     * 
     * @param context
     *            the context.
     * @param attrs
     *            the attribute set.
     */
    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        /*
         * UI
         */

        setDialogLayoutResource(R.layout.dialog_number_picker);
        setDialogIcon(null);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(null);

        /*
         * VALUES
         */

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.NumberPickerPreference);

        mMin = a.getInt(R.styleable.NumberPickerPreference_min, 0);
        mMax = a.getInt(R.styleable.NumberPickerPreference_max, 0);
        mIncrement = a.getInt(R.styleable.NumberPickerPreference_increment, 0);

        a.recycle();

        mCurrentValue = getPersistedInt(DEFAULT_VALUE);

        updateUI();
    }// NumberPickerPreference()

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        getDialog().setCanceledOnTouchOutside(true);
    }// showDialog()

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        /*
         * MAP CONTROLS
         */

        mTextNumber = (TextView) view.findViewById(R.id.text_number);
        mBtnDecrease = view.findViewById(R.id.button_decrease);
        mBtnIncrease = view.findViewById(R.id.button_increase);

        /*
         * INIT CONTROLS
         */

        mBtnDecrease.setOnClickListener(mBtnChangeValueOnClickListener);
        mBtnIncrease.setOnClickListener(mBtnChangeValueOnClickListener);

        updateUI();
    }// onBindDialogView()

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, DEFAULT_VALUE);
    }// onGetDefaultValue()

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue,
            Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            mCurrentValue = getPersistedInt(DEFAULT_VALUE);
        } else {
            // Set default state from the XML attribute
            mCurrentValue = (Integer) defaultValue;
            persistInt(mCurrentValue);
        }

        updateUI();
    }// onSetInitialValue()

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistInt(mCurrentValue);
        }
    }// onDialogClosed()

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        // Check whether this Preference is persistent (continually saved)
        if (isPersistent()) {
            // No need to save instance state since it's persistent, use
            // superclass state
            return superState;
        }

        // Create instance of custom BaseSavedState
        final SavedState myState = new SavedState(superState);
        // Set the state's value with the class member that holds current
        // setting value
        myState.mValue = mCurrentValue;
        return myState;
    }// onSaveInstanceState()

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // Check whether we saved the state in onSaveInstanceState()
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save the state, so call superclass
            super.onRestoreInstanceState(state);
            return;
        }

        // Cast state to custom BaseSavedState and pass to superclass
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        // Set this Preference's widget to reflect the restored state
        mCurrentValue = myState.mValue;
        updateUI();
    }// onRestoreInstanceState()

    private static class SavedState extends BaseSavedState {

        int mValue;

        public SavedState(Parcelable superState) {
            super(superState);
        }// SavedState()

        public SavedState(Parcel source) {
            super(source);
            // Get the current preference's mValue
            mValue = source.readInt();
        }// SavedState()

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            // Write the preference's mValue
            dest.writeInt(mValue);
        }// writeToParcel()

        /*
         * Standard creator object using an instance of this class.
         */
        @SuppressWarnings("unused")
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }// createFromParcel()

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }// newArray()
        };// CREATOR
    }// SavedState

    /**
     * Update the UI.
     */
    private void updateUI() {
        setSummary(String.format("%,d", mCurrentValue));

        if (mTextNumber != null) {
            mTextNumber.setText(getSummary());
            mBtnDecrease.setEnabled(mCurrentValue > mMin);
            mBtnIncrease.setEnabled(mCurrentValue < mMax);
        }
    }// updateUI()

    /*
     * LISTENERS
     */

    private final View.OnClickListener mBtnChangeValueOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.button_decrease)
                mCurrentValue -= mIncrement;
            else
                mCurrentValue += mIncrement;
            updateUI();
        }// onClick()
    };// mBtnChangeValueOnClickListener
}
