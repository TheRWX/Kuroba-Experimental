# Vibeuroba

**An Android imageboard browser maintained by [TheRWX](https://github.com/TheRWX).**

Vibeuroba brings browsing, posting, bookmarks, and media downloads into one app.
Read boards and threads, follow conversations, view images and videos, and save
threads for later. It is a continuation of
[Kuroba Experimental](https://github.com/K1rakishou/Kuroba-Experimental), which is
itself a fork of [Kuroba](https://github.com/Adamantcheese/Kuroba).

This fork starts its own public version series at **1.0**, under the
**Vibeuroba** project name. Its Git history and upstream attribution are preserved.

## What the app does

- **Browse and search:** open boards and threads in tabs, search supported sites,
  and combine multiple boards into a single catalog.
- **Follow conversations:** bookmark threads, organize bookmarks into groups,
  and receive thread update notifications.
- **Post replies:** attach media from your device, shared files, or a URL;
  queue replies with the background posting service.
- **View media:** open images, GIFs, and videos in a dedicated viewer. Optional
  mpv playback requires a separate download.
- **Save content:** download media in the background and export downloaded
  threads as HTML with their media files.
- **Customize your experience:** use dynamic light and dark themes, content
  filters, and per-site proxy settings.
- **Read archives:** access supported third-party thread archives.

The inherited site integrations include 4chan, Dvach, 8kun, Lainchan, Endchan,
and others. Available features depend on each site's API, permissions, and
availability; inclusion in the source does not guarantee that a service is
currently accessible.

## The 1.0 starting point

TheRWX is developing Vibeuroba as an independent continuation, with more
features to follow. The `vibeuroba-1.0` branch builds on the existing
`KurobaEx_2.0.0` development work, including the Vibeuroba branding and ongoing
Jetpack Compose interface work.

This first pass establishes the name, version, and project documentation.
The capabilities above come from the existing codebase; they are not a claim
that every feature has been newly implemented or tested for 1.0.

Before publishing a 1.0 APK, the remaining release work is to:

- Verify the Android build and exercise browsing, posting, media, and downloads
  on a device.
- Adapt the inherited updater and release scripts to Vibeuroba's version series
  and release destinations.
- Confirm release signing and the upgrade path from previously installed builds.

## Download and installation

Published APKs are listed on this fork's
[Releases page](https://github.com/TheRWX/Vibeuroba/releases).
**Setting this branch to 1.0 does not publish a 1.0 APK.** Check the release title
and attached assets before installing. The upstream project's APKs and F-Droid
listing are separate distributions.

The app targets devices running Android 5.0 (API 21) or newer. Its existing
Android application ID is retained. Installing over a previous build requires
a compatible signing key; the displayed version alone does not establish
upgrade compatibility.

## Build from source

The Android project lives in [`Kuroba/`](Kuroba/). Its configured toolchain uses
JDK 17, Android SDK 34, and the included Gradle wrapper. Dependencies are fetched
from Google Maven, Maven Central, and JitPack.

```sh
git clone --branch vibeuroba-1.0 https://github.com/TheRWX/Vibeuroba.git
cd Vibeuroba/Kuroba
```

Set your SDK location in an untracked `local.properties` file:

```properties
sdk.dir=/absolute/path/to/your/android-sdk
```

Build the development APK:

```sh
./gradlew :app:assembleDevDebug
```

The expected output is `app/build/outputs/apk/dev/debug/Vibeuroba-dev.apk`.
The `dev` flavor has a separate application ID suffix so it can coexist with
the stable flavor. Release builds need your own signing configuration.

The public version is `1.0`. Android's internal `versionCode` is `10336`, one
higher than the previous build, so the public version reset does not lower
Android's install ordering. Keep incrementing that internal code for future
releases. The inherited updater still derives version codes from upstream-style
tags, so it must be adapted before publishing releases in the new series.

## Screenshots

These screenshots are inherited from Kuroba Experimental and illustrate the
existing app; they are not verified screenshots of the Vibeuroba 1.0 build.

<details>
<summary>View the inherited screenshot gallery</summary>

[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/1.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/1.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/2.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/2.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/3.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/3.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/4.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/4.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/5.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/5.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/6.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/6.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/7.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/7.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/8.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/8.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/9.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/9.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/10.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/10.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/11.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/11.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/12.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/12.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/13.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/13.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/14.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/14.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/15.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/15.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/16.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/16.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/17.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/17.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/18.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/18.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/19.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/19.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/20.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/20.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/21.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/21.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/22.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/22.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/23.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/23.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/24.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/24.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/25.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/25.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/26.png" width=160>](fastlane/metadata/android/en-US/images/phoneScreenshots/26.png)

</details>

## Feedback and contributions

Use this fork's [issue tracker](https://github.com/TheRWX/Vibeuroba/issues)
for Vibeuroba feedback. Include the app version, Android version, affected site,
and steps to reproduce a problem. Discuss larger features before implementing
them so they fit the direction of this fork.

## Credits and license

Vibeuroba builds on the work of K1rakishou, the Kuroba Experimental contributors,
and the original Kuroba contributors. Existing copyright notices and attribution
remain in the source.

Licensed under [GNU GPL v3](COPYING.txt). See also the
[third-party library licenses](Kuroba/app/src/main/assets/html/license.html).
