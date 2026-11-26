# Local original-client evidence collection

Use this on a PC where the original APK is present. Keep the APK, extracted native libraries, OBB, and full disassembly private; do not commit them.

## Prerequisites

- PowerShell 7 or Windows PowerShell
- JADX CLI available as `jadx` on PATH
- LLVM tools available on PATH: `llvm-readelf`, `llvm-objdump`, `llvm-strings`

## Run

From the repository root:

```powershell
.\quest-roblox-vr\reverse-engineering\scripts\inspect-roblox2014-apk.ps1 -ApkPath "C:\path\to\Roblox2014.apk" -OutputDir "C:\temp\roblox2014-analysis"
```

If LLVM/JADX aren't on PATH, pass their executable paths via `-LlvmReadelf`, `-LlvmObjdump`, `-LlvmStrings`, and `-Jadx`.

## Send back only the focused evidence

From the output folder, search the disassembly/symbol dump for:

- `JNI_OnLoad`
- `Java_com_roblox_client_ActivityGlView_nativeStartUpGraphics`
- `Java_com_roblox_client_ActivityGlView_nativeStartGame`
- `Java_com_roblox_client_ActivityGlView_nativeShutDownGraphics`
- `Java_com_roblox_client_ActivityGlView_nativeStopGame`

From JADX output, find the original `ActivityGlView` and `ActivityNativeMain` class/methods. The important methods are constructor, `onCreate`, `surfaceCreated`, `surfaceChanged`, `surfaceDestroyed`, and any launch helper. Include call order and exact argument construction, but redact tokens, account data, URLs containing secrets, and unrelated private data.

## Caveats

The script collects static evidence; it does not execute the legacy APK or make the ARM32 native library compatible with Quest. LLVM disassembly is a starting point. Native control flow must be traced from entry addresses and calls, and indirect calls/JNI registrations may need additional analysis.
