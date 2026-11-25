# Recovered 2014 APK startup path (direct APK analysis)

**Source analyzed:** `com.roblox.client_2.178.54547_HiAppHere_com(2).apk` supplied privately in chat. No APK/OBB/native payload is committed here.

## Package facts

- Package: `com.roblox.client`
- Version name: `2.178.54547`, version code `26`
- min SDK 15; target SDK 19
- Manifest launcher: `com.roblox.client.ActivityNativeMain`
- Service: `com.roblox.client.RobloxService`, non-exported, process `:service`
- Required OpenGL ES: 2.0
- APK contains one native ABI: `lib/armeabi/` (ARM32)
- `libroblox.so`: ELF32 ARM, about 19.4 MB
- `libfmodex.so`: ELF32 ARM, about 1.0 MB
- Embedded OBB: `assets/main.1.com.roblox.client.obb`, about 24.4 MB
- OBB ZIP has 687 entries and approximately 48.3 MB uncompressed; payload categories include `content/`, `android/`, and `shaders/`. Content contains fonts/meshes, textures, sounds, scripts, sky assets, particles and music.

## Activity and service lifecycle (DEX evidence)

1. Manifest launches `ActivityNativeMain`.
2. `RobloxServiceActivity.onCreate()` calls Activity `onCreate`, checks Google Play Services, obtains `StoreManager`, and starts the explicit `RobloxService`.
3. `RobloxServiceActivity.onStart()` binds to `RobloxService` with `BIND_AUTO_CREATE`. It includes intent extra `roblox_launcher_debugger_attached`.
4. The Messenger ServiceConnection registers the activity Messenger (message code 1), then reports service-connected notification 1.
5. `RobloxService.onCreate()` calls `XAPKManager.unpackAssetsAsync(context)` on a worker thread.
6. The GL activity is `ActivityGlView`. Its `onCreate()` validates launch/process state, loads native libraries `fmodex` then `roblox`, initializes HttpAgent and RobloxSettings, inflates its layout, creates its edit-text helper and SurfaceView, registers the SurfaceHolder callback, and creates FMODAudioDevice.
7. `ActivityGlView.initSurfaceView()` configures the SurfaceView focus flags and adds its SurfaceHolder callback.
8. `ActivityGlView.surfaceCreated()` sends service notification 1 (GL surface ready), applies the pause/resume guard, gets display density, obtains the holder Surface, and calls `nativeStartUpGraphics(surface, density)`.
9. Service place-ID notification 3 reaches `ActivityGlView.onServiceNotification(notification, payload)`. The handler logs the place ID, performs asset unpack/validation, gets the Surface and display density, checks `android.hardware.touchscreen`, then calls `nativeStartGame(surface, String.valueOf(payload), unpackedContentPath, density, hasTouchscreen)`. It creates `InputListener` and installs it as the SurfaceView touch listener.
10. `surfaceDestroyed()` calls `nativeShutDownGraphics(surface)`, then `nativeStopGame()` (with the original logging and critical-error guard).

The exact DEX method declaration for the game-start call is:

```java
nativeStartGame(Surface, String, String, float, boolean)
```

Descriptor:
`(Landroid/view/Surface;Ljava/lang/String;Ljava/lang/String;FZ)V`

The graphics-start declaration is:
`nativeStartUpGraphics(Surface, float)`, descriptor `(Landroid/view/Surface;F)V`.

## OBB unpacking behavior

`XAPKManager` static constants include:

- OBB asset name: `main.1.com.roblox.client.obb`
- Fingerprint file: `fingerprint.txt`
- Buffer size: 8192 bytes

`unpackAssetsAsync(context)` creates and starts a Thread with a Runnable that calls the synchronous unpack routine.

`unpackAssets(context)`:
1. Acquires a lock.
2. Uses `context.getDir("assets", 0)` for app-private extraction.
3. Reads the OBB fingerprint and the existing extracted-assets fingerprint.
4. If fingerprints do not match or are missing, deletes/recreates the assets directory, calls `unzipContainer(context, directory)`, and writes the fingerprint.
5. Returns the path `<app-private-assets-dir>/content`.
6. Logs either an error if the OBB cannot be validated or `Assets are up to date`.

`unzipContainer` opens the embedded OBB through `AssetManager.open`, wraps it in `ZipInputStream`, iterates ZIP entries, creates directories/files, and streams bytes to output. The APK contains the OBB as an asset; this path is not a conventional external-storage OBB mount.

## Native/JNI contract recovered from DEX

`ActivityGlView` native methods:
- `nativeCallMessagesFromMainThread()`
- `nativeHandleBackPressed()`
- `nativeInGamePurchaseFinished(boolean, long, String)`
- `nativeOnLowMemory()`
- `nativePassText(long, String)`
- `nativeReleaseFocus(long)`
- `nativeShutDownGraphics(Surface)`
- `nativeStartGame(Surface, String, String, float, boolean)`
- `nativeStartUpGraphics(Surface, float)`
- `nativeStopGame()`
- `nativeVideoAdFinished(boolean)`

`InputListener` native methods:
- `nativeCameraRotate(float, float)`
- `nativeCharacterJump()`
- `nativeCharacterMove(float, float)`
- `nativeFireWeapon(float, float)`
- `nativePassAccelerometerChange(float, float, float)`
- `nativePassGravityChange(float, float, float)`
- `nativePassGyroscopeChange(float, float, float, float, float, float, float)`
- `nativePassInput(int, int, int, int, int, int)`
- Gesture entry points (long-press, pan, pinch, rotate, swipe, tap)
- `nativeSetAccelerometerEnabled(boolean)`
- `nativeSetGyroscopeEnabled(boolean)`
- `nativeSwitchToWeaponPos(int)`
- `nativeToggleThirdPersonCam(boolean)`

`RobloxSettings` native methods include base URL, cache/files directories, cookies, proxy, platform user-agent, version, Breakpad, FastLog and profiler controls.

## Confirmed native limitation

Both original shared libraries are ARM32 `armeabi` ELF binaries. They are not ARM64 libraries and cannot be relabeled, copied into `arm64-v8a`, or loaded in a 64-bit-only Quest process. JNI/DEX recovery establishes the Java contract; it does **not** translate the proprietary native engine into ARM64 or recover its source.

## Next reverse-engineering targets

- Disassemble the rest of `ActivityNativeMain` and anonymous listener classes to recover how login/home launches `ActivityGlView` and how launch extras are populated.
- Resolve the notification switch and verify each notification branch, including the exact payload type for place launch.
- Inspect native startup functions in the ARM32 ELF for further platform/renderer clues, while treating binary analysis as reference only.
- Locate a legitimate ARM64 source/build route. Only then implement Quest/OpenXR renderer, stereo swapchain, tracking, controller input, and testing.

## Reproducibility and privacy

This report was derived from the user-provided APK using ZIP inspection, DEX table/class-data inspection, JNI symbol inspection, and ELF header inspection. Keep the original APK, OBB, and proprietary binaries private; do not commit them.
