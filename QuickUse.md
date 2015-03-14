# Integrating (Eclipse IDE) #

**First,** you have to open this project in Eclipse. Assuming you place this library at `/data/projects/android-lockpattern`.

**If** you use Ant: to re-generate your `local.properties`, open terminal, use `android` tool (in SDK):
```
# [Android SDK]/tools/android update project -p /data/projects/android-lockpattern/code --target android-19
```

**Then, import** this library into your project:

  * By Eclipse: Right click on your project, select _Properties_, select _Android_ tab, then add this library to _Library_ box.
  * Manual: Open your _project.properties_ file, add this line:
```
android.library.reference.1=/data/projects/android-lockpattern/code
```
> _Note:_ `1` is the sequence number of the library. Reset it to fit your project. Perhaps it starts from `1`, I don't know  :-D

**Import** [LockPatternActivity](http://docs.android-lockpattern.googlecode.com/hg/com/haibison/android/lockpattern/LockPatternActivity.html) into your application:
  * By Eclipse: Open `AndroidManifest.xml` -> tab _Application_ -> box _Application Nodes_, click _Add_, select `Activity`. Go to box _Attributes for Activity_, click _Browse_ (next to field _Name_), then add `LockPatternActivity`.
  * Manual: Open `AndroidManifest.xml`, inside tag `application`, add this:
```
<activity
    android:name="com.haibison.android.lockpattern.LockPatternActivity"
    android:theme="@style/Alp.42447968.Theme.Dark" />
```

**Note** that there are 5 built-in themes:

  * `Alp.42447968.Theme.Dark`
  * `Alp.42447968.Theme.Light`
  * `Alp.42447968.Theme.Light.DarkActionBar` (available from API 7+, but only works from API 14+)
  * `Alp.42447968.Theme.Dialog.Dark`
  * `Alp.42447968.Theme.Dialog.Light`

You _have to_ use one of them in order to let the library work properly. Because the themes contain resources that the library needs.

**Android** team recommends that you should set your target SDK to the newest one. You can still use the library for Android 1.6+. For example:
```
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="android.dumdum"
    android:versionCode="19"
    android:versionName="2.7" >

    <uses-sdk
        android:minSdkVersion="4"
        android:targetSdkVersion="19" />
    ...
```

  * To avoid the activity from being killed after screen orientation changed, add this:
```
<activity
    android:name="com.haibison.android.lockpattern.LockPatternActivity"
    android:configChanges="orientation|screenSize|keyboard|keyboardHidden"
    android:screenOrientation="user"
    android:theme="@style/Alp.42447968.Theme.Dark" />
```

# Usage #

## Create new pattern ##

```
...
// This is your preferred flag
private static final int REQ_CREATE_PATTERN = 1;

...

Intent intent = new Intent(LockPatternActivity.ACTION_CREATE_PATTERN, null,
        your-context, LockPatternActivity.class);
startActivityForResult(intent, REQ_CREATE_PATTERN);
```

For more secure, you might want to use [Encryption](Encryption.md).

## And to get the result ##

```
@Override
protected void onActivityResult(int requestCode, int resultCode,
        Intent data) {
    switch (requestCode) {
    case REQ_CREATE_PATTERN: {
        if (resultCode == RESULT_OK) {
            char[] pattern = data.getCharArrayExtra(
                    LockPatternActivity.EXTRA_PATTERN);
            ...
        }
        break;
    }// REQ_CREATE_PATTERN
    }
}
```

## Let the user identify himself with lock pattern ##

```
...
// This is your preferred flag
private static final int REQ_ENTER_PATTERN = 2;

...

char[] savedPattern = ...

Intent intent = new Intent(LockPatternActivity.ACTION_COMPARE_PATTERN, null,
        your-context, LockPatternActivity.class);
intent.putExtra(LockPatternActivity.EXTRA_PATTERN, savedPattern);
startActivityForResult(intent, REQ_ENTER_PATTERN);
```
```
...
@Override
protected void onActivityResult(int requestCode, int resultCode,
        Intent data) {
    switch (requestCode) {
    case REQ_ENTER_PATTERN: {
        /*
         * NOTE that there are 4 possible result codes!!!
         */
        switch (resultCode) {
        case RESULT_OK:
            // The user passed
            break;
        case RESULT_CANCELED:
            // The user cancelled the task
            break;
        case LockPatternActivity.RESULT_FAILED:
            // The user failed to enter the pattern
            break;
        case LockPatternActivity.RESULT_FORGOT_PATTERN:
            // The user forgot the pattern and invoked your recovery Activity.
            break;
        }

        /*
         * In any case, there's always a key EXTRA_RETRY_COUNT, which holds
         * the number of tries that the user did.
         */
        int retryCount = data.getIntExtra(
                LockPatternActivity.EXTRA_RETRY_COUNT, 0);

        break;
    }// REQ_ENTER_PATTERN
    }
}
```

## Notes ##

  * You can tell the library to store newly created pattern into [SharedPreferences](http://developer.android.com/reference/android/content/SharedPreferences.html). So you might not need to specify pattern for action [ACTION\_COMPARE\_PATTERN](http://docs.android-lockpattern.googlecode.com/hg/com/haibison/android/lockpattern/LockPatternActivity.html#ACTION_COMPARE_PATTERN). By default, this flag is off. To turn it on:
```
import com.haibison.android.lockpattern.util.Settings;

...

Settings.Security.setAutoSavePattern(your-context, true);
```
  * [ACTION\_COMPARE\_PATTERN](http://docs.android-lockpattern.googlecode.com/hg/com/haibison/android/lockpattern/LockPatternActivity.html#ACTION_COMPARE_PATTERN): there are _**4 possible result codes**_ like the sample above.
  * [ACTION\_VERIFY\_CAPTCHA](http://docs.android-lockpattern.googlecode.com/hg/com/haibison/android/lockpattern/LockPatternActivity.html#ACTION_VERIFY_CAPTCHA): there are _**3 possible result codes**_ like the sample above _excluding_ [RESULT\_FORGOT\_PATTERN](http://docs.android-lockpattern.googlecode.com/hg/com/haibison/android/lockpattern/LockPatternActivity.html#RESULT_FORGOT_PATTERN).

# Tips #

## Themes ##

  * To turn `LockPatternActivity` into a dialog, open _**your**_ `AndroidManifest.xml` and add this:
```
<activity
    android:name="com.haibison.android.lockpattern.LockPatternActivity"
    android:theme="@style/Alp.42447968.Theme.Dialog.Dark" />
```

  * To change theme in runtime, you must set theme in `AndroidManifest.xml` as above. If you don't do that and set theme to dialog via code, the background of `LockPatternActivity` will be _not_ transparent. A correct example:
```
...
intent.putExtra(LockPatternActivity.EXTRA_THEME, R.style.Alp_42447968_Theme_Dialog_Dark);
```

## Stealth Mode ##

In stealth mode, there will be no visible feedback as the user enters the pattern. To turn it on, use helper class [Settings.Display](http://docs.android-lockpattern.googlecode.com/hg/com/haibison/android/lockpattern/util/Settings.Display.html). By default this mode if off.
```
import com.haibison.android.lockpattern.util.Settings;

...

Settings.Display.setStealthMode(your-context, true);
```

## CAPTCHA ##

To let the library generate a random [CAPTCHA](http://en.wikipedia.org/wiki/CAPTCHA) pattern and have the user verify it, you can use action [ACTION\_VERIFY\_CAPTCHA](http://docs.android-lockpattern.googlecode.com/hg/com/haibison/android/lockpattern/LockPatternActivity.html#ACTION_VERIFY_CAPTCHA). Default wired dots is `4`, you can change it with helper class `Settings.Display`.
```
...
Settings.Display.setCaptchaWiredDots(your-context, 9);
Intent intent = new Intent(LockPatternActivity.ACTION_VERIFY_CAPTCHA, null,
        your-context, LockPatternActivity.class);
startActivityForResult(intent, your-request-code);
```

## Forgot pattern? ##

If you use [ACTION\_COMPARE\_PATTERN](http://docs.android-lockpattern.googlecode.com/hg/com/haibison/android/lockpattern/LockPatternActivity.html#ACTION_COMPARE_PATTERN), the user might forget his/ her pattern. So, you can use a `PendingIntent` of your `Activity` to help the user recover the pattern when he/she needs. For example:

```
PendingIntent piForgotPattern = ...

Intent intentActivity = new Intent(
        LockPatternActivity.ACTION_COMPARE_PATTERN, null,
        your-context, LockPatternActivity.class);
intentActivity.putExtra(
        LockPatternActivity.EXTRA_PENDING_INTENT_FORGOT_PATTERN,
        piForgotPattern);
...
```

When the user taps the button _"Forgot pattern?"_ (on `LockPatternActivity`), the library _makes a call_ to start your pending intent, then finishes itself with [RESULT\_FORGOT\_PATTERN](http://docs.android-lockpattern.googlecode.com/hg/com/haibison/android/lockpattern/LockPatternActivity.html#RESULT_FORGOT_PATTERN).

## Action Bar Icons ##

Version 3+ includes an icon set for action bar, which has dark and light icons.

| ![https://android-lockpattern.googlecode.com/hg/code/res/drawable-xhdpi/alp_42447968_ic_action_lockpattern_dark.png](https://android-lockpattern.googlecode.com/hg/code/res/drawable-xhdpi/alp_42447968_ic_action_lockpattern_dark.png) | ![https://android-lockpattern.googlecode.com/hg/code/res/drawable-xhdpi/alp_42447968_ic_action_lockpattern_light.png](https://android-lockpattern.googlecode.com/hg/code/res/drawable-xhdpi/alp_42447968_ic_action_lockpattern_light.png) |
|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

If you use one of built-in themes (`R.style.Alp_42447968_Theme_*`) for your activity, you can make a reference to the icon like:

```
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android" >

    <item
        android:id="@+id/action_lockpattern"
        android:icon="?attr/alp_42447968_ic_action_lockpattern"
        ... />

</menu>
```

Or if you use your own themes, use the icons directly instead of that reference. For example:

```
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <style name="AppTheme.Dark">
        <item name="alp_ic_action_lockpattern">@drawable/alp_42447968_ic_action_lockpattern_light</item>
    </style>

    <style name="AppTheme.Light">
        <item name="alp_ic_action_lockpattern">@drawable/alp_42447968_ic_action_lockpattern_dark</item>
    </style>

</resources>
```

If you want more, the SVG source file is available [here](https://code.google.com/p/android-lockpattern/source/browse/resources/images/controls/alp_42447968_ic_action_lockpattern.svg).

# Other Notes #

## Default language ##

Default language is English. It is located at `res/values/strings.xml`. Actually it is a copy of `res/values-en/strings.xml`. So you can change it to your preferred language.

If you'd like to contribute your translation, we thank you  :-)

## `AndroidManifest.xml` ##

All configurations with helper class [Settings](http://docs.android-lockpattern.googlecode.com/hg/com/haibison/android/lockpattern/util/Settings.html) (and all of its nested classes) must be called before you start `LockPatternActivity`. But you _can_ configure settings in an activity and call `LockPatternActivity` in another activity.

You can also configure settings directly via your app's [AndroidManifest.xml](AndroidManifest.md).