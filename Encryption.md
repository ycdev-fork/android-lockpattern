It will be more secure if for each app, you have a unique encrypter — to encrypt the pattern on your own way.

# Details #

For example, here is a simple encrypter (which is used in the demo app), we name it `LPEncrypter`:
```
package ...

import com.haibison.android.lockpattern.collect.Lists;
import com.haibison.android.lockpattern.util.IEncrypter;
import com.haibison.android.lockpattern.widget.LockPatternView.Cell;

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

    @Override
    public List<Cell> decrypt(Context context, char[] encryptedPattern) {
        List<Cell> result = Lists.newArrayList();
        String[] ids = new String(encryptedPattern).split("[^0-9]");
        for (String id : ids)
            result.add(Cell.of(Integer.parseInt(id) - 1));

        return result;
    }// decrypt()

}
```

Usage:
```
import com.haibison.android.lockpattern.util.Settings;

...

Settings.Security.setEncrypterClass(your-context, LPEncrypter.class);
```

Or via [AndroidManifest.xml](AndroidManifest.md):
```
<activity
    android:name="com.haibison.android.lockpattern.LockPatternActivity"
    android:configChanges="keyboard|keyboardHidden|orientation|screenSize"
    android:screenOrientation="user"
    android:theme="@style/Alp.42447968.Theme.Dialog.Dark" >
    <meta-data
        android:name="encrypterClass"
        android:value="...full.qualified.name.to.your.LPEncrypter" />
</activity>
```

You might want to take a look at built-in class [SimpleWeakEncryption](http://docs.android-lockpattern.googlecode.com/hg/com/haibison/android/lockpattern/util/SimpleWeakEncryption.html). However, like its name suggests, it is just a _simple and weak_ encryption utility. Use it on your own risk  :-)

# Notes #

  * Classes implementing [IEncrypter](http://docs.android-lockpattern.googlecode.com/hg/com/haibison/android/lockpattern/util/IEncrypter.html) must have one zero-argument constructor. Otherwise, an [InvalidEncrypterException](http://docs.android-lockpattern.googlecode.com/hg/com/haibison/android/lockpattern/util/InvalidEncrypterException.html) will be thrown.