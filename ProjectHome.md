&lt;wiki:gadget url="https://android-lockpattern.googlecode.com/hg/resources/gadgets/main\_header.xml" height="70" width="50%" border="1" /&gt;

Android has one useful tool in security settings, it is Lock Pattern. Users can define their own lock pattern ‒ which is a combination of 4+ dots.

We have extracted this piece of code from Android source code, [platform/frameworks/base](https://android.googlesource.com/platform/frameworks/base/+/master/core/java/com/android/internal/widget/). Then modified it a little, to make it easy to use in an application.

  * **Latest stable: v3.2 (#45)** (January 19th, 2015). _From version `3.1.1 beta (#44)`, this project follows [Semantic Versioning](https://en.wikipedia.org/wiki/Semantic_versioning#Semantic_versioning)._
  * The demo can be downloaded on [Google Play](https://play.google.com/store/apps/details?id=group.pals.android.lib.ui.lockpattern.demo). It needs some more permissions to help us monetize it. With this service we are able to create more great apps and libraries for you guys. _Thank you for your understanding._
  * Latest source is available in the demo app. You can export it directly to SD card or to Google Drive, Dropbox...

### Features ###

  * Requires: Android 2.1+ (API 7+).
  * Supports: Eclipse IDE, Android Studio (beta).
  * _No dependencies._
  * Designed for both _phones and tablets_.
  * Stealth mode (invisible pattern).
  * 5 built-in themes:
    * Dark/Light
    * Light with dark action bar (available from API 7+, but only works from API 14+)
    * Dark/Light dialogs
  * Ability to generate and let the user verify CAPTCHA pattern.

### Notes ###

  * If you're upgrading the library, refer to [Migration Notes](MigrationNotes.md) first.
  * For more information about usage, see Wiki pages. Or you might want to see [Quick Use](QuickUse.md).
  * You're welcome to file new issues on [Issues](https://code.google.com/p/android-lockpattern/issues/list) section.