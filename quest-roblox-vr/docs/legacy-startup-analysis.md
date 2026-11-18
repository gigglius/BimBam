# Roblox 2014 Android startup analysis

## Current project state

- `MainActivity` is a layout-only recreation of the 2014 menu; it does not launch the engine.
- `ActivityGlView` contains JNI declarations but no native library loading, SurfaceView lifecycle, or calls to `nativeStartGame`.
- `RobloxServiceActivity` is an empty stub, not the original Messenger service implementation.
- The Android app module does not package `libroblox.so`, `libfmodex.so`, or the original OBB.

## Confirmed legacy engine facts

The archived Roblox 2014 APK contained `lib/armeabi/libroblox.so`, `lib/armeabi/libfmodex.so`, and `assets/main.1.com.roblox.client.obb`. The native engine is ELF32 ARM / `armeabi`.

**Quest 3 blocker:** Meta's current Quest documentation requires 64-bit app binaries; a 32-bit-only native library cannot run on Quest. Simply copying the old `.so` files into this APK or calling `System.loadLibrary` cannot restore startup. The engine must be rebuilt/ported from source for `arm64-v8a`, or replaced by a separately licensed compatible engine.

## Confirmed activity lifecycle

1. `RobloxServiceActivity` constructor initializes a Messenger backed by an inner `IncomingHandler`, an empty pending-message list, a bind counter, and a nullable StoreManager reference.
2. `onCreate` calls the Android Activity implementation, checks Google Play Services, obtains `StoreManager.getStoreManager(context)`, and starts the explicit `RobloxService` service.
3. `onStart` calls `doBindService()`. That method binds to `RobloxService` with `BIND_AUTO_CREATE` and includes the `roblox_launcher_debugger_attached` intent extra.
4. When the service connects, its Messenger is used to flush queued messages. `onServiceConnected()` flushes pending messages and clears the queue.
5. `doNotifyService(notification, extra)` sends message code 3 with the two integer arguments, or queues the message until the service is connected.
6. `doUnbindService()` sends message code 2 with the activity Messenger as `replyTo`, then unbinds.
7. `onResume` calls `StoreManager.handleActivityResume()`.
8. `onActivityResult` delegates to `StoreManager.handleActivityResult()` and falls back to the Android Activity implementation when unhandled.
9. `onStop` unbinds the service, invokes `Utils.contextSwitchExclusiveDialog(context)`, then calls the Android Activity implementation.

## Recovered source artifact: RobloxServiceActivity$1

The repository contains `RobloxServiceActivity1.txt`, a decompiled smali listing for the anonymous `ServiceConnection` inner class. It independently confirms the service handshake:

1. `onServiceConnected(ComponentName, IBinder)` wraps the binder in an Android `Messenger` and stores it on the enclosing activity.
2. It obtains message code 1, sets `replyTo` to the activity's Messenger, and sends the registration message to the service.
3. It calls `doNotifyService(1, 0)` to send the connected notification.
4. It then invokes the enclosing activity's `onServiceConnected()` callback and increments the bind counter.
5. `onServiceDisconnected` only logs the disconnect in this inner class.

This file is useful original Android glue evidence, but it is **not** the missing `ActivityGlView`, `ActivityNativeMain`, `RobloxService`, or native engine source. It does not reveal the arguments passed to `nativeStartGame`.

## Required original dependencies

The superclass references `RobloxService`, `RobloxServiceActivity$IncomingHandler`, `RobloxServiceActivity$1` (ServiceConnection), `StoreManager`, `Utils`, and Google Play Services utility `GooglePlayServicesUtil`. Recreating only the superclass cannot restore startup.

## Confirmed RobloxService behavior

- `RobloxService` extends Android `Service` and exposes a Messenger binder from `onBind()`.
- Its constructor creates `RobloxService$IncomingHandler`, an empty client-Messenger list, and initializes the current place ID to `-1` (`INVALID_PLACE_ID`).
- The bind intent extra `roblox_launcher_debugger_attached` enables the service's wait-for-debugger flag.
- `onCreate()` calls `XAPKManager.unpackAssetsAsync(context)`; asset unpacking is part of service startup.
- Message constants: register client = 1, unregister client = 2, notification = 3.
- Notification constants: connected = 1, GL shutdown = 2, set place ID = 3, get place ID = 4.
- The service broadcasts notification messages (message code 3, with notification and extra integers); dead clients are removed after a `RemoteException`.
- GL shutdown resets the current place ID to `-1`; `onStartCommand()` returns `START_NOT_STICKY`.

## Confirmed Messenger handler behavior

- Service handler message 1 registers `msg.replyTo`; message 2 unregisters it; message 3 interprets `arg1` as notification type and `arg2` as payload.
- Notification 2 delegates to GL shutdown. Notification 3 updates the current place ID and broadcasts it. Notification 4 broadcasts the current place ID.
- Activity handler message 3 forwards `arg1` and `arg2` to `onServiceNotification`.
- On connection, the activity wraps the binder in a Messenger, sends message 1 to register itself, sends notification 1 (connected), then invokes the activity's `onServiceConnected()` callback.

## Implemented safe preflight

`LegacyEngineBootstrap` reports supported ABIs and checks for the two expected native libraries and the original OBB in the app-specific OBB directory. `MainActivity` writes this report to Logcat at launch. This is intentionally a diagnostic gate, not a fake engine launch: it never loads the incompatible binary.

## Quest 3 / VR constraints and next milestone

- Do not package or load the original 32-bit `armeabi` engine on Quest 3.
- A real Quest 3 engine requires original engine source plus an arm64 port/rebuild, or a separately licensed compatible engine.
- Even with an arm64 engine, the legacy SurfaceView renderer is not automatically OpenXR: stereo rendering, head pose, controller input, and VR frame submission must be implemented separately.
- The exact native launch arguments and surface lifecycle must be recovered from the matching `ActivityGlView.smali` and `ActivityNativeMain.smali` before invoking JNI.
- Keep proprietary native libraries and original OBB assets private; do not commit them to this public repository.

## Next source needed

The next useful source files are the matching decompiled text for `ActivityGlView.smali`, `ActivityNativeMain.smali`, `RobloxService.smali`, `XAPKManager.smali`, and `StoreManager.smali`. The original 32-bit binary by itself is insufficient for a Quest 3 port.
