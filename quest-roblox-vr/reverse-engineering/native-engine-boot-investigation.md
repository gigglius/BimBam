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


## JNI symbol mapping and first-frame trace status (2026-11-26)

### Java bridge declarations ↔ exported native symbols

The report records these exports from the original ARM32 `libroblox.so`. JNI name-based mapping can be matched for the following declarations where the names and class match:

| Java native declaration | Exported symbol | Match status |
|---|---|---|
| `ActivityGlView.nativeStartGame` | `Java_com_roblox_client_ActivityGlView_nativeStartGame` | Name/class match; body/address not analyzed |
| `ActivityGlView.nativeStartUpGraphics` | `Java_com_roblox_client_ActivityGlView_nativeStartUpGraphics` | Name/class match; body/address not analyzed |
| `ActivityGlView.nativeShutDownGraphics` | `Java_com_roblox_client_ActivityGlView_nativeShutDownGraphics` | Name/class match; body/address not analyzed |
| `ActivityGlView.nativeStopGame` | `Java_com_roblox_client_ActivityGlView_nativeStopGame` | Name/class match; body/address not analyzed |
| `ActivityGlView.nativeCallMessagesFromMainThread` | `Java_com_roblox_client_ActivityGlView_nativeCallMessagesFromMainThread` | Name/class match; body/address not analyzed |
| `ActivityGlView.nativePassText` | `Java_com_roblox_client_ActivityGlView_nativePassText` | Name/class match; body/address not analyzed |
| `ActivityGlView.nativeReleaseFocus` | `Java_com_roblox_client_ActivityGlView_nativeReleaseFocus` | Name/class match; body/address not analyzed |
| `ActivityGlView.nativeOnLowMemory` | `Java_com_roblox_client_ActivityGlView_nativeOnLowMemory` | Name/class match; body/address not analyzed |

Additional exported JNI functions reported by the original library, not currently declared in the reconstructed Java bridge:

- `ActivityGlView.nativePassInput`
- `InputListener.nativeCharacterMove`
- `InputListener.nativeCharacterJump`
- `InputListener.nativeCameraRotate`
- `InputListener.nativeFireWeapon`
- `RobloxSettings.nativeSetBaseUrl`
- `RobloxSettings.nativeSetCacheDirectory`
- `RobloxSettings.nativeSetFilesDirectory`

The report's current Java bridge has 11 declarations; eight are matched by exported name/class in this list. Three declarations (`nativeHandleBackPressed`, `nativeInGamePurchaseFinished`, `nativeVideoAdFinished`) do not appear in the recovered export list. This is not proof they are absent from the binary: they could be dynamically registered, omitted from the earlier symbol extraction, or be declarations from another client revision. Verify with a full symbol/registration scan and DEX class/signature comparison.

### Native initialization function

`JNI_OnLoad` is confirmed as an exported symbol in the investigation report. Its implementation has not been disassembled, so it is not yet confirmed whether it registers methods, initializes global runtime state, or performs any engine startup. The three graphics/game JNI functions are the visible candidate entry points, but none is proven to be the engine's internal initialization routine.

### Surface → graphics trace

Current verified facts: Java declarations accept an Android `Surface`; repository `SurfaceHolder.Callback` methods are empty; the original smali files are not in the repo. Thus the original `Surface` conversion/retention, EGL/native-window acquisition, thread, and call ordering are unknown.

### Scene, camera, render, and presentation

No recovered disassembly or runtime trace currently identifies when a scene becomes drawable, when a camera is attached/initialized, the first render function, or the buffer presentation/swap function. Do not label guessed engine internals as recovered. These are blocked on native code/disassembly and, for launch order, matching DEX method bodies.

### Evidence checklist to close the trace

1. Full `libroblox.so` dynamic and static symbol output; JNI_OnLoad disassembly and any `RegisterNatives` table.
2. Native disassembly/call graph for the five JNI startup/graphics functions: `nativeStartUpGraphics`, `nativeStartGame`, `nativeCallMessagesFromMainThread`, `nativeShutDownGraphics`, `nativeStopGame`.
3. DEX/smali bodies for `ActivityNativeMain`, `ActivityGlView`, service readiness and Surface callbacks to prove Java ordering and arguments.
4. Renderer call graph from graphics initialization to first draw and presentation; identify camera/scene readiness by code paths and data flow, not guessed names.

The legacy library is ARM32. This investigation is for source/compatibility understanding; it is not a runnable Quest 3 native engine. Do not load the original library into the arm64 Quest process.
