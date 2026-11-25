# Quest 3 native-engine port work plan

## Objective
Run the original 2014 Roblox client engine on Meta Quest 3 with a native VR presentation path. This is a real engine port, not a menu mockup.

## Verified repository baseline (2026-11-25)
- Android app is a Java shell and menu mockup; it does not launch the engine.
- `ActivityGlView` has JNI declarations only; native startup is deliberately disabled.
- `RobloxServiceActivity` is a stub.
- Preflight logging exists in `LegacyEngineBootstrap`.
- Recovered native engine and FMOD binaries are ELF32 ARM (`armeabi`); Quest 3 requires 64-bit native app code. They cannot be loaded directly into the Quest app process.
- Original client OBB is private/proprietary; do not commit APK, OBB, or native libraries.
- Exact native launch contract is not fully recovered. Never invoke JNI using guessed arguments.

## Work tracks

### A — provenance and legal build inputs (blocking)
1. Identify exact client build using APK manifest, DEX metadata, and native build strings.
2. Recover matching DEX launch path: `ActivityNativeMain`, `ActivityGlView`, surface callbacks, service/asset unpack flow, and native call arguments.
3. Locate source/build inputs or an authorized ARM64 engine build. ARM32 shared objects cannot be mechanically converted to ARM64.
4. Document evidence for every recovered fact (file, method, symbol, address).

### B — Android shell
1. Add dedicated engine host and surface lifecycle state machine.
2. Separate Activity, Surface, engine, and service lifecycles.
3. Queue lifecycle events until bridge initialization.
4. Keep native startup gated until ARM64 engine and exact JNI contract are available.
5. Add structured logs and crash breadcrumbs.

### C — graphics / VR
1. Verify native graphics API and EGL requirements.
2. Identify actual main render target/presentation path before VR injection.
3. Implement OpenXR Quest session, stereo swapchains, eye views/projection, tracking spaces, and frame timing in engine render loop.
4. Keep diagnostic non-VR path if possible.
5. Validate compositor submission, frame pacing, thermal behavior, and sustained performance.

### D — input / gameplay
1. Map Quest buttons, sticks, triggers, grip to legacy input events.
2. Convert headset pose to camera transforms with explicit coordinate conventions.
3. Add comfort settings (snap/smooth turn, movement-relative orientation, sensitivity, invert vertical look).
4. Separate gameplay and UI input layers.

### E — assets, services, networking
1. Reimplement platform glue only from verified behavior; don't invent message contracts.
2. Validate OBB, extraction, cache paths, permissions, and lifecycle.
3. Verify service/network compatibility experimentally; don't claim compatibility without evidence.

## Milestones / exit criteria
- M0: reproducible cloud build; preflight clearly reports missing/incompatible inputs.
- M1: app launches on Quest; lifecycle/surface diagnostics captured.
- M2: legally buildable ARM64 engine loads and initializes.
- M3: first actual engine frame displayed (not placeholder).
- M4: stable stereo OpenXR rendering and head tracking.
- M5: controller input and basic camera/character movement.
- M6: original menu flow and playable experience end-to-end.
- M7: stability, performance, thermal, and package acceptance.

## Immediate next task
Set up repeatable cloud build for Android shell, then inspect private APK/DEX locally and document the launch path. CI success proves the shell compiles only—not that the original engine runs.
