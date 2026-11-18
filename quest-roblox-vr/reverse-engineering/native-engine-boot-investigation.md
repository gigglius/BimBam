# Native engine boot investigation (December 2014 client)

## APK identity and recovered files

The uploaded APK is named `com.roblox.client_2.178.54547_HiAppHere_com(1).apk`, but the ZIP entry timestamps for the client payload are from 2014-12-11/13. The APK contains:

- `classes.dex` (3,596,816 bytes)
- `lib/armeabi/libroblox.so` (19,433,880 bytes)
- `lib/armeabi/libfmodex.so` (1,045,372 bytes)
- `assets/main.1.com.roblox.client.obb` (24,407,692 bytes)

Do not infer the client generation from the third-party filename alone; confirm it against the embedded manifest, DEX strings, and native metadata.

## Confirmed native ABI

`readelf -h` reports `libroblox.so` as ELF32, machine ARM. The library is a 32-bit ARM EABI shared object, not `arm64-v8a`. The FMOD library is also ELF32 ARM. This is a hard blocker for directly loading these libraries in the Quest 3 64-bit-only app process.

## JNI exports recovered from libroblox.so

The dynamic symbol table exposes the following relevant JNI entry points:

- `JNI_OnLoad`
- `Java_com_roblox_client_ActivityGlView_nativeStartGame`
- `Java_com_roblox_client_ActivityGlView_nativeStartUpGraphics`
- `Java_com_roblox_client_ActivityGlView_nativeShutDownGraphics`
- `Java_com_roblox_client_ActivityGlView_nativeStopGame`
- `Java_com_roblox_client_ActivityGlView_nativeCallMessagesFromMainThread`
- `Java_com_roblox_client_ActivityGlView_nativePassInput`
- `Java_com_roblox_client_ActivityGlView_nativePassText`
- `Java_com_roblox_client_ActivityGlView_nativeReleaseFocus`
- `Java_com_roblox_client_ActivityGlView_nativeOnLowMemory`
- `Java_com_roblox_client_InputListener_nativeCharacterMove`
- `Java_com_roblox_client_InputListener_nativeCharacterJump`
- `Java_com_roblox_client_InputListener_nativeCameraRotate`
- `Java_com_roblox_client_InputListener_nativeFireWeapon`
- `Java_com_roblox_client_RobloxSettings_nativeSetBaseUrl`
- `Java_com_roblox_client_RobloxSettings_nativeSetCacheDirectory`
- `Java_com_roblox_client_RobloxSettings_nativeSetFilesDirectory`

This corrects an earlier shorthand: the exported `nativeStartGame` symbol is attached to `ActivityGlView`, not `ActivityNativeMain`.

## DEX evidence

Strings in `classes.dex` confirm class references for `ActivityNativeMain`, `ActivityGlView`, `RobloxService`, and `XAPKManager`. They also include the native method names above and lifecycle/debug strings such as `ActivityGlView onCreate()`, `nativeStartUpGraphics`, and `nativeStopGame`.

A strings scan does **not** recover method bodies, JNI parameter descriptors, or the exact ordering/arguments used to launch the engine. Those must be obtained by disassembling `classes.dex` (for example with JADX or baksmali) and inspecting the matching methods.

## Boot path to recover next

1. Decode `classes.dex` and inspect `ActivityNativeMain` creation/intent handoff.
2. Inspect `ActivityGlView.onCreate`, surface callbacks, and its calls to `nativeStartUpGraphics` and `nativeStartGame`.
3. Record the exact Java native method declarations and argument order from the DEX.
4. Trace how `RobloxService` and `XAPKManager` unpack and locate the OBB payload.
5. Reconstruct the original Android boot sequence only as reference; do not invoke the old ARM32 binaries on Quest 3.

## What is required for a real Quest boot

The original native source code (or another legally usable source/build path) is required to produce an ARM64 build and adapt the renderer to Quest/OpenXR. JNI symbol names and a 32-bit binary are not enough to mechanically convert the engine to ARM64. The Quest app should remain behind a clear unsupported-engine gate until an ARM64 engine exists; do not add a fake native launch.

## Safety / distribution

Keep the original APK, OBB, and proprietary shared libraries private. Do not commit those binaries to the public BimBam repository. Commit only analysis notes and independently authored compatibility code.
