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

## Phase status (2026-11-25)

| Phase | Status | Exit criteria |
|---|---|---|
| 0 — Repository/build setup | **Partially complete** | Reproducible CI, source tree and build artifacts; remaining setup is cleanup and build reproducibility |
| 1 — Original startup recovery | **Active / incomplete** | Exact Activity launch order, surface lifecycle, OBB/service protocol, Teleporter object flow, native init and first-frame boundary |
| 2 — ARM64 native engine | **BLOCKED / decision required** | Authorized ARM64 source/build input or a scoped compatible replacement engine |
| 3 — Quest platform/VR | Not started | OpenXR Quest session, lifecycle, tracking, input, Android permissions and storage |
| 4 — Rendering | Not started | Engine frame rendered into stereo OpenXR swapchains with measured frame pacing |
| 5 — Original client UI/assets | Not started | Verified menu/login/home/profile/browser flows and asset loading |
| 6 — Gameplay/network | Not started | Character movement, camera, physics, interactions and server/network path |
| 7 — QA/release | Not started | Installable signed build, test matrix, performance/stability and release notes |

### Phase 2 decision gate

The recovered native engine and FMOD are 32-bit ARM ELF. Quest 3's native app environment is ARM64. There is no safe or realistic binary conversion step that turns those files into ARM64 libraries.

Investigate these routes in this order:

1. **Authorized matching source + ARM64 build system:** preferred; build the engine and FMOD/platform dependencies for Android ARM64, then verify the JNI bridge.
2. **Authorized vendor-provided ARM64 runtime:** only if its provenance, API and client-build compatibility are verified.
3. **Engine/platform replacement:** implement a compatible client runtime and rendering/gameplay layer using a legal, documented interface. This is a new engine compatibility project, not a recovered original binary.
4. **32-bit execution/compatibility experiments:** do not assume Quest supports this; prove device/OS/runtime support before investing. Even if process compatibility exists, VR integration and legacy graphics dependencies remain separate blockers.

**Do not** rename ARM32 libraries, fake ABI metadata, package the original engine into a 64-bit APK and expect it to load, or enable guessed JNI calls.

## Immediate work queue

1. Obtain a reliable DEX disassembler (JADX or baksmali) in a repeatable workflow and save verified smali for ActivityNativeMain, ActivityGlView, RobloxService, XAPKManager, and related service/asset classes.
2. Trace the native Teleporter instance from allocation through vptr installation, ownership/retention, and the indirect callback invocation. Record address, caller, arguments and confidence.
3. Cross-reference native exports with verified Java descriptors and recover the startup call graph; identify graphics init, game start, shutdown and first-frame boundary.
4. Research and document lawful ARM64 source/build availability and dependency status. Phase 2 cannot be declared complete without an actual ARM64 engine build or a chosen replacement runtime.
5. Keep CI building the shell and add automated tests for lifecycle gating, asset validation, ABI checks and crash diagnostics. CI success is shell validation only.

CI success proves the shell compiles only—not that the original engine runs.