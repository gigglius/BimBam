# APK launcher-to-engine transition addendum

Analyzed from the user-supplied APK's `classes.dex`.

## Launcher to WebView

`ActivityNativeMain.onMainImageButton(view)` calls `getUrl(view)`. When a URL is returned, it constructs an Intent for `ActivityWebView`, sets:

- `roblox_url` = returned URL
- `roblox_back_activity` = `ActivityNativeMain`

It starts the WebView activity and finishes the shell Activity.

## WebView to native game Activity

`ActivityWebView.onServiceNotification(notification, payload)` has a notification-3 branch. It converts the integer payload to a string and constructs an Intent for `ActivityGlView`, setting:

- `roblox_placeid` = payload as string
- `roblox_launcher_pid` = current process ID
- `roblox_launcher_debugger_attached` = debugger-connected boolean
- `roblox_back_activity` = `ActivityWebView`

It starts `ActivityGlView` and finishes the WebView Activity.

## GL activity launch validation

`ActivityGlView.onCreate()` reads `roblox_launcher_pid` and `roblox_launcher_debugger_attached`, logs the incoming intent, and checks that it was launched in the same process. If debugger-attached is true, it invokes `Debug.waitForDebugger()` as part of the original launch behavior.

The rest of the GL Activity setup loads `fmodex` and `roblox`, initializes HttpAgent and native settings, and constructs the SurfaceView/audio device.

## Important distinction

This confirms the Java launcher handoff and the JNI contract. It does not recover native engine source or create an ARM64 library. The recovered native payload remains ARM32.
