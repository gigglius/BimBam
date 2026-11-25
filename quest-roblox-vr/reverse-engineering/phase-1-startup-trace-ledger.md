# Phase 1 Startup Trace Ledger

Branch: work/quest-native-engine-port

## Evidence rules
- Confirm claims only from disassembly, source, symbols, runtime traces, or reproducible tests.
- Record exact method names, descriptors, call order, thread, and arguments.
- Do not invoke guessed JNI signatures.
- Do not load/package the legacy ARM32 engine on Quest 3.

## Checkpoints
| ID | Task | Status | Next action |
|---|---|---|---|
| P1-01 | APK/DEX/native build identity | Partial | Verify embedded metadata; filename alone is insufficient |
| P1-02 | Native ABI inventory | Confirmed | Engine and FMOD are ELF32 ARM/armeabi |
| P1-03 | JNI export inventory | Confirmed | Exports documented in native-engine-boot-investigation.md |
| P1-04 | ActivityNativeMain flow | Open | Disassemble and record launch/call chain |
| P1-05 | ActivityGlView.onCreate | Open | Record exact calls, args, threads, order |
| P1-06 | Surface lifecycle callbacks | Open | Trace surface/EGL ownership and native calls |
| P1-07 | nativeStartUpGraphics contract | Open | Recover descriptor and caller arguments |
| P1-08 | nativeStartGame contract | Open | Recover descriptor and caller arguments |
| P1-09 | Shutdown/focus/low-memory calls | Open | Map lifecycle events to JNI |
| P1-10 | OBB extraction | Partial | Inspect XAPKManager extraction/completion logic |
| P1-11 | Messenger protocol | Partial | Confirm semantics for messages 1, 2, 3 and payloads |
| P1-12 | Teleporter vtable installation | Open | Find allocation, vptr assignment and retention |
| P1-13 | Teleporter callback caller | Open | Trace object flow to indirect dispatch callsite |
| P1-14 | Startup reachability | Open | Connect Activity, surface, service/assets, JNI and boot |
| P1-15 | Engine init / first frame | Open | Identify initialization boundary and first actual world draw |

## Next sequence
1. Disassemble ActivityNativeMain, ActivityGlView, RobloxService, XAPKManager and StoreManager.
2. Capture JNI descriptors and callsites.
3. Cross-reference Java lifecycle with native exports/callsites.
4. Trace Teleporter allocation/vptr installation to callback dispatch.
5. Build evidence-backed startup graph and list uncertainties.
6. Continue ARM64/source strategy investigation in parallel.

## Phase 1 exit criteria
- [ ] Exact Java launch sequence and lifecycle recovered.
- [ ] JNI signatures and arguments verified.
- [ ] OBB extraction and service protocol understood.
- [ ] Teleporter vtable/object/callback flow traced.
- [ ] Engine initialization and first-frame boundary identified.
- [ ] Startup graph backed by evidence.
