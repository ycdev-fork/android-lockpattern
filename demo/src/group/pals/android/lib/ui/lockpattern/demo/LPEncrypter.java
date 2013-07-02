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

import group.pals.android.lib.ui.lockpattern.util.IEncrypter;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternView.Cell;

import java.util.List;

import android.content.Context;

public class LPEncrypter implements IEncrypter {

    @Override
    public char[] encrypt(Context context, List<Cell> pattern) {
        /*
         * This is a simple example. And it's also worth mentioning that this is
         * a very weak encrypter, just for fun :-)
         */

        StringBuilder result = new StringBuilder();
        for (Cell cell : pattern)
            result.append(Integer.toString(cell.getId() + 1)).append('-');

        return result.substring(0, result.length() - 1).toCharArray();
    }// encrypt()
}
