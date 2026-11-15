# Roblox 2014 Android startup analysis

Source inspected: `RobloxServiceActivity.smali` uploaded from the archived client.

## Confirmed activity lifecycle

1. Constructor initializes a Messenger backed by an inner `IncomingHandler`, an empty pending-message list, a bind counter, and a nullable StoreManager reference.
2. `onCreate` calls the Android Activity implementation, checks Google Play Services, obtains `StoreManager.getStoreManager(context)`, and starts the explicit `RobloxService` service.
3. `onStart` calls `doBindService()`. That method binds to `RobloxService` with `BIND_AUTO_CREATE` and includes the `roblox_launcher_debugger_attached` intent extra.
4. When the service connects, its Messenger is used to flush queued messages. `onServiceConnected()` flushes pending messages and clears the queue.
5. `doNotifyService(notification, extra)` sends message code 3 with the two integer arguments, or queues the message until the service is connected.
6. `doUnbindService()` sends message code 2 with the activity Messenger as `replyTo`, then unbinds.
7. `onResume` calls `StoreManager.handleActivityResume()`.
8. `onActivityResult` delegates to `StoreManager.handleActivityResult()` and falls back to the Android Activity implementation when unhandled.
9. `onStop` unbinds the service, invokes `Utils.contextSwitchExclusiveDialog(context)`, then calls the Android Activity implementation.

## Required original dependencies

The superclass is not self-contained. It references at least:

- `com.roblox.client.RobloxService`
- `com.roblox.client.RobloxServiceActivity$IncomingHandler`
- `com.roblox.client.RobloxServiceActivity$1` (ServiceConnection implementation)
- `com.roblox.client.StoreManager`
- `com.roblox.client.Utils`
- Google Play Services utility class `GooglePlayServicesUtil`

The Messenger protocol and service callbacks are implemented partly in those missing classes. Recreating only this superclass would not restore the original startup correctly.

## Quest 3 / VR constraints

- The recovered `libroblox.so` is ELF32 ARM (armeabi), not arm64-v8a. Do not package or load it on Quest 3 until device ABI support is verified.
- The recovered ActivityGlView is a SurfaceView-based GLES activity. This alone does not provide OpenXR, stereoscopic rendering, headset pose tracking, or Quest controller input.
- The original Android activity/service stack also depends on legacy Google Play Services and StoreManager behavior that may no longer be available or compatible.
- Do not publish the proprietary Roblox .so libraries or original OBB assets in this public repository.

## Next source needed to reconstruct startup

Upload these decompiled files as text from the original APK:

- `com/roblox/client/RobloxServiceActivity$1.smali`
- `com/roblox/client/ActivityNativeMain.smali`
- `com/roblox/client/ActivityGlView.smali`

Then inspect the StoreManager and Utils method signatures before implementing compatibility classes. Avoid inventing message codes, extras, or native startup arguments.

## Confirmed RobloxService behavior

Source inspected: `RobloxService.smali` (temporary upload; deleted after inspection).

- `RobloxService` extends Android `Service` and exposes a `Messenger` binder from `onBind()`.
- Its constructor creates `RobloxService$IncomingHandler`, an empty list of client Messengers, and initializes the current place ID to `-1` (`INVALID_PLACE_ID`).
- The activity's bind intent extra `roblox_launcher_debugger_attached` enables the service's wait-for-debugger flag.
- `onCreate()` calls `XAPKManager.unpackAssetsAsync(context)`; asset unpacking is therefore part of service startup.
- Message constants: register client = 1, unregister client = 2, notification = 3.
- Notification constants: connected = 1, GL shutdown = 2, set place ID = 3, get place ID = 4.
- The service keeps registered client Messengers and broadcasts notification messages (message code 3, with notification and extra integers). Dead clients are removed after a `RemoteException`.
- GL shutdown resets the current place ID to `-1`.
- `onStartCommand()` returns `2` (`START_NOT_STICKY`).

This confirms that the service is responsible for asset unpacking and coordinating activity clients/place-ID state; it is not the native rendering engine itself.

## Confirmed RobloxService$IncomingHandler behavior

Source inspected: `RobloxService$IncomingHandler.smali` (temporary upload; deleted after inspection).

- The handler first checks whether debugger waiting was requested and, if it has not already waited, logs a warning and calls `Debug.waitForDebugger()`. It then marks the wait as completed so it does not repeat.
- Incoming message code 1 registers `msg.replyTo` in the service's client-Messenger list.
- Incoming message code 2 unregisters `msg.replyTo`.
- Incoming message code 3 treats `arg1` as the notification type and `arg2` as its integer payload.
- Notification type 2 delegates to the service's GL-shutdown handler.
- Notification type 3 sets the current place ID from `arg2`, broadcasts notification type 3 with that place ID to clients, and logs the new ID.
- Notification type 4 logs the requested place ID value and broadcasts notification type 3 with the service's current place ID. The incoming `arg2` is logged but is not used as the returned place ID.
- Other notification types are delegated to the service's general notification handler.
- Unknown message codes are passed to Android's base `Handler.handleMessage()`.

The service protocol is now clearer: activities register/unregister through Messenger, and notifications carry a type plus an integer payload. The place-ID query response is broadcast using notification type 3 with the service's stored current place ID.

## Confirmed RobloxServiceActivity$IncomingHandler behavior

Source inspected: `RobloxServiceActivityIncomingHandler.txt` (temporary upload; deleted after inspection).

- The handler switches on the incoming message's `what` field.
- Message code 3 reads `arg1` as the notification type and `arg2` as the integer payload.
- It calls the enclosing activity's `onServiceNotification(arg1, arg2)` method, then logs both integer values.
- All other message codes are passed to Android's base `Handler.handleMessage()`.

This completes the activity-side Messenger callback path: the service broadcasts message code 3, and the activity handler forwards its two integer arguments to the activity's notification callback.
