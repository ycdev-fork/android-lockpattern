All configurations with helper class [Settings](http://docs.android-lockpattern.googlecode.com/hg/com/haibison/android/lockpattern/util/Settings.html) (and all of its nested classes) must be called before you start [LockPatternActivity](http://docs.android-lockpattern.googlecode.com/hg/com/haibison/android/lockpattern/LockPatternActivity.html). But you _can_ configure settings in an activity and call `LockPatternActivity` in another activity.

However that could make the usage more complicated. So there's an option: You can also configure settings directly via your app's `AndroidManifest.xml`. For details, refer to class `Settings`. Note that the values in the manifest get higher priority than the ones from this class.

For example, this is used in the demo app:

```
<activity
    android:name="com.haibison.android.lockpattern.LockPatternActivity"
    android:configChanges="keyboard|keyboardHidden|orientation|screenSize"
    android:screenOrientation="user"
    android:theme="@style/Alp.42447968.Theme.Dialog.Dark" >
    <meta-data
        android:name="autoSavePattern"
        android:value="true" />
    <meta-data
        android:name="encrypterClass"
        android:value="group.pals.android.lib.ui.lockpattern.demo.LPEncrypter" />
</activity>
```

If you use ProGuard _and_ set your encrypter class via manifest like above, remember to ignore your encrypter class:
```
-keep class group.pals.android.lib.ui.lockpattern.demo.LPEncrypter { *; }
```