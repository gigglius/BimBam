# DEX JNI contract inventory (APK-local analysis)

Source: uploaded APK's classes.dex (DEX 035). This report records declaration-level facts only; it does not reconstruct all method bodies or prove runtime reachability.

## Confirmed native declarations — ActivityGlView

| Java method | Descriptor | Meaning |
|---|---|---|
| nativeStartGame | (Surface, String, String, float, boolean) -> void | Native game start entry |
| nativeStartUpGraphics | (Surface, float) -> void | Native graphics startup |
| nativeShutDownGraphics | (Surface) -> void | Graphics shutdown |
| nativeStopGame | () -> void | Stop native game |
| nativeCallMessagesFromMainThread | () -> void | Main-thread message pump |
| nativePassInput | (int, int, int, int, int) -> void | Input bridge (descriptor needs exact DEX inventory verification; export exists) |
| nativePassText | (long, String) -> void | Text input bridge |
| nativeReleaseFocus | (long) -> void | Focus release |
| nativeOnLowMemory | () -> void | Low-memory callback |
| nativeHandleBackPressed | () -> void | Back-key handling |
| nativeInGamePurchaseFinished | (boolean, long, String) -> void | Purchase callback |
| nativeVideoAdFinished | (boolean) -> void | Video-ad callback |

**Descriptors directly verified in the DEX method table:**  
- nativeStartGame: `(Landroid/view/Surface;Ljava/lang/String;Ljava/lang/String;FZ)V`
- nativeStartUpGraphics: `(Landroid/view/Surface;F)V`
- nativeShutDownGraphics: `(Landroid/view/Surface;)V`
- nativeStopGame: `()V`
- nativeCallMessagesFromMainThread: `()V`
- nativePassText: `(JLjava/lang/String;)V`
- nativeReleaseFocus: `(J)V`
- nativeOnLowMemory: `()V`

The native `nativePassInput` export was previously found in libroblox.so, but this report does not yet verify its declaration descriptor. Do not rely on the tentative table row above; its descriptor remains **unverified**.

## Bytecode call references observed

The DEX contains call references to:
- `nativeStartGame(Surface, String, String, float, boolean)`
- `nativeStartUpGraphics(Surface, float)`
- `XAPKManager.unpackAssetsAsync(Context)`
- `XAPKManager.unpackAssets(Context) -> String`
- `SurfaceView.getHolder() -> SurfaceHolder`
- `SurfaceHolder.getSurface() -> Surface`
- `Resources.getDisplayMetrics()`

The startup caller method names and full register-to-argument mapping need a proper DEX disassembler. Our lightweight exploratory decoder is not a trustworthy full disassembly.

## ABI constraint

The APK's native engine and FMOD libraries are ELF32 ARM (`armeabi`). This is not compatible with Quest 3's 64-bit-only application native code. Exact JNI descriptors help reconstruct the Java bridge but do not solve ARM64 engine availability.

## Next required evidence
1. Obtain a real DEX disassembler (JADX/baksmali) and generate verified smali for ActivityGlView and ActivityNativeMain.
2. Capture the caller method names, argument values/registers, thread, and lifecycle order for graphics/game startup.
3. Trace XAPKManager extraction and service readiness.
4. Cross-reference each declaration with the matching native export and native callsite.
