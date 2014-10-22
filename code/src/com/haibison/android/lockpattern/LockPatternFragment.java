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

package com.haibison.android.lockpattern;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haibison.android.lockpattern.util.UI;

/**
 * Fragment implementation for {@link LockPatternActivity}.
 * 
 * @author Hai Bison
 * 
 */
public class LockPatternFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), UI.resolveAttribute(
                getActivity(), R.attr.alp_42447968_theme_dialog));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(initContentView(dialog.getLayoutInflater(), null));
        return dialog;
    }// onCreateDialog()

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return getDialog() != null ? null
                : initContentView(inflater, container);
    }// onCreateView()

    /**
     * Loads content view from XML and init controls.
     * 
     * @param inflater
     *            {@link LayoutInflater}.
     * @param container
     *            {@link ViewGroup}.
     */
    private View initContentView(LayoutInflater inflater, ViewGroup container) {
        /*
         * LOADS CONTROLS
         */

        View rootView = inflater.inflate(
                R.layout.alp_42447968_lock_pattern_activity, container, false);

        return rootView;
    }// initContentView()

}
