# Quest 3 engine port: current hard blockers

This is a private sideload project. Store submission work is out of scope.

## What is in the recovered 2014 package

The supplied decompiled package contains:

- `lib/armeabi/libroblox.so` — ELF32 ARM / legacy `armeabi`
- `lib/armeabi/libfmodex.so` — ELF32 ARM / legacy `armeabi`
- `assets/main.1.com.roblox.client.obb` — 687 ZIP entries
- Android Java/Dex application glue in the original APK (the decompiled ZIP by itself does not include the complete Java source tree)

The OBB passed ZIP integrity testing. Its MD5 is `9e11c2d97cc194d1516cbac0dfc133de`, matching the APK fingerprint. The native engine imports Android, EGL, GLESv2, logging, FMOD, zlib, math, libc, and dl APIs.

## Why the original engine cannot simply be copied into the Quest APK

Meta's current Quest native build guidance specifies `arm64-v8a` only. The supplied native libraries are 32-bit ARM ELF files. Changing the manifest, target SDK, Gradle ABI filters, or JNI declarations cannot translate ARM machine code into AArch64.

A real engine port requires one of:

1. The matching original Roblox engine source and build system, plus an ARM64 toolchain and source-level port; or
2. A legally usable compatible engine implementation that can consume the original game data and implement the required Roblox runtime behavior.

The original binary is useful as a behavioral/reverse-engineering reference, but it is not a source-level ARM64 port.

## Current implementation status

- The Android shell and menu are Java-only.
- The JNI bridge class is declarative only; it does not load or call the legacy native libraries.
- No ARM64 Roblox engine or ARM64 FMOD library is present in the repository.
- The original OBB is not committed to the public repository.
- The runtime preflight verifies ARM64 availability, presence of the required ARM64 libraries, and the OBB MD5 before any native launch is enabled.
- The first-frame target remains the original GLES/EGL renderer. OpenXR head tracking and controller support remain deferred until that frame is proven.

## Next engineering milestones

1. Obtain the exact matching source/build tree for the 2014 native client, or establish a legally usable engine replacement.
2. Build the smallest ARM64 native bootstrap and resolve every non-system dependency, beginning with FMOD.
3. Reconstruct the original Java startup contract: activity/service lifecycle, native method descriptors, surface callbacks, settings paths, and OBB extraction/access.
4. Port the original EGL/GLES renderer to the Quest surface and reproduce one successful frame.
5. Add stereo rendering and OpenXR pose/input integration only after the original renderer presents reliably.

Do not package the original ARM32 shared libraries in the Quest APK, and do not mark the engine as launched until the native runtime actually initializes and presents a frame.
