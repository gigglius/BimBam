# ActivityGlView Startup Contract Reconstruction

Date: 2026-11-25

This document separates repository-confirmed Java/JNI declarations from unverified original APK behavior. It is not a recovered original smali listing.

## 1. Current repository state

- `MainActivity.onCreate()` hides Android system UI, logs `LegacyEngineBootstrap.inspect(this)`, and calls `render()`.
- `MainActivity` is a menu shell. It does not instantiate or launch `ActivityGlView`.
- `ActivityGlView` extends the local stub `RobloxServiceActivity` and implements `SurfaceHolder.Callback`.
- Its Surface callbacks are currently empty.
- The app has not restored the original native libraries, service implementation, OBB, or engine launch contract.

## 2. JNI declarations (confirmed in repository)

```java
private static native void nativeCallMessagesFromMainThread();
private static native void nativeHandleBackPressed();
private static native void nativeInGamePurchaseFinished(
    boolean success, long productId, String receipt);
private static native void nativeOnLowMemory();
private static native void nativePassText(long textFieldId, String text);
private static native void nativeReleaseFocus(long textFieldId);
private static native void nativeShutDownGraphics(Surface surface);
private static native void nativeStartGame(
    Surface surface, String firstArgument, String secondArgument,
    float density, boolean flag);
private static native void nativeStartUpGraphics(Surface surface, float density);
private static native void nativeStopGame();
private static native void nativeVideoAdFinished(boolean finished);
```

JNI descriptors (Java types):
- `nativeStartUpGraphics`: `(Landroid/view/Surface;F)V`
- `nativeStartGame`: `(Landroid/view/Surface;Ljava/lang/String;Ljava/lang/String;FZ)V`
- `nativeShutDownGraphics`: `(Landroid/view/Surface;)V`
- `nativeStopGame`: `()V`

These signatures are declared in our reconstruction; original binary symbol binding and exact original calls still require matching APK/native artifacts.

## 3. Lifecycle state model to implement (not claimed as original)

```
Activity created
  -> superclass service setup (original behavior must be recovered)
  -> ActivityGlView / rendering view installed
  -> Surface becomes available
  -> validate Surface + engine/asset readiness
  -> native graphics startup [order and conditions unverified]
  -> native game startup [arguments unverified]
  -> main-thread message pump [mechanism/frequency unverified]
  -> surface loss / Activity stop
  -> native graphics shutdown and game stop [ordering unverified]
```

## 4. JNI contract questions

| Parameter | Known | Unknown |
|---|---|---|
| Surface | Android Surface passed to native | Whether native retains it; thread and lifetime requirements |
| String 1 | Java String | Meaning, format, nullability, default |
| String 2 | Java String | Meaning, format, nullability, default |
| density | float | Which display metric is used; when sampled |
| boolean | boolean | Meaning and launch configuration |
| graphics startup | method declaration exists | Exact call site, repeat behavior, required EGL/context |
| game startup | method declaration exists | Call site, argument values, thread, return/error behavior |

## 5. Safe implementation guardrails

- Do not call JNI while the native library is absent or ABI-incompatible.
- Do not invent launch strings or boolean meaning.
- Do not call graphics startup repeatedly for the same Surface unless original behavior or a tested state machine requires it.
- Track Surface identity and lifecycle explicitly.
- Keep JNI calls on the recovered/original thread model; thread affinity is not yet known.
- On Quest 3, an old 32-bit ARM library cannot be used as a normal native library in a 64-bit-only app. A compatible arm64 build/port is required.
- Surface lifecycle alone does not provide OpenXR stereo, headset tracking, or Quest controller support.

## 6. Required evidence to close this reconstruction

1. Matching APK's `ActivityGlView.smali`: constructor, `onCreate`, `surfaceCreated`, `surfaceChanged`, `surfaceDestroyed`, and any game-start helper.
2. `ActivityNativeMain.smali` or related launch Activity: intent extras and launch argument construction.
3. Native library JNI exports / registration table: map declarations to native methods and confirm library loading.
4. Service/assets code: OBB extraction readiness and synchronization before game startup.
5. Runtime logs from a compatible original device, if available, to establish ordering and failures.

## 7. Current confidence

- Menu Activity doesn't launch engine: confirmed from repository source.
- JNI declaration shapes: confirmed in repository source.
- Service/Messenger lifecycle and asset unpacking: recovered in existing analysis notes, but underlying complete source is not currently available in this working context for independent re-verification.
- Surface callback order and native call order: unknown.
- Native launch arguments and boolean semantics: unknown.
- First rendered frame path: unknown.
