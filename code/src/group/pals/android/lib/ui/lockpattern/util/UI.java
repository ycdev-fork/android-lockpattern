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

package group.pals.android.lib.ui.lockpattern.util;

import group.pals.android.lib.ui.lockpattern.R;
import android.app.Dialog;
import android.view.Window;

/**
 * UI utilities.
 * 
 * @author Hai Bison
 * 
 */
public class UI {

    /**
     * Uses a fixed size for {@code dialog} in large screens.
     * 
     * @param dialog
     *            the dialog.
     */
    public static void adjustDialogSizeForLargeScreen(Dialog dialog) {
        adjustDialogSizeForLargeScreen(dialog.getWindow());
    }// adjustDialogSizeForLargeScreen()

    /**
     * Uses a fixed size for {@code dialogWindow} in large screens.
     * 
     * @param dialogWindow
     *            the window <i>of the dialog</i>.
     */
    public static void adjustDialogSizeForLargeScreen(Window dialogWindow) {
        if (dialogWindow.isFloating()
                && dialogWindow.getContext().getResources()
                        .getBoolean(R.bool.alp_is_large_screen))
            dialogWindow.setLayout(
                    dialogWindow
                            .getContext()
                            .getResources()
                            .getDimensionPixelSize(
                                    R.dimen.alp_dialog_width_large),
                    dialogWindow
                            .getContext()
                            .getResources()
                            .getDimensionPixelSize(
                                    R.dimen.alp_dialog_height_large));
    }// adjustDialogSizeForLargeScreen()
}
