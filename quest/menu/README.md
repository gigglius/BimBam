# Original Roblox menu preservation

## Decision

Use the menu already implemented in the 2014 Roblox native engine. Do not replace it with a newly designed Quest menu.

## Evidence in the uploaded APK

The original `lib/armeabi/libroblox.so` contains these menu-related strings and symbols:

- `N3RBX10TopMenuBarE` — native Roblox `RBX::TopMenuBar` type.
- `ChatMenuPanel` — native chat/menu UI panel.
- `PlaceFilter_EnableAndroidMenuLeave` and `EnableAndroidMenuLeave` — place-filter setting controlling the Android leave-menu behavior.
- `N3RBX13LeaveGameVerbE` — native leave-game verb.
- `menuState == SHOWN_APPEARING`, `menuState == HOVER`, and `menuState == NOTHING` — menu state diagnostics from `Client/App/gui/GUI.cpp`.

These are evidence that the APK has its own native in-game GUI/menu system. The Android launcher layouts (for example `activity_native_main.xml`) are separate from this in-game menu.

## Quest integration direction

1. Preserve the original `RBX::TopMenuBar` and associated native GUI implementation.
2. Keep its existing visual assets, layout, labels, and menu behavior.
3. Adapt Quest controller input to the original GUI input/hit-testing path rather than drawing a replacement menu.
4. Route the Quest system/menu button to the existing native menu-open/back behavior, investigating `nativeHandleBackPressed()` and the engine's Android menu-leave setting.
5. Keep menu rendering on the preserved engine GUI renderer; the OpenXR graphics bridge should change frame presentation and stereo camera handling, not replace the menu.

## Current limitation

The uploaded APK contains a stripped native binary, not the original C++ source. The strings above identify the native menu components and settings, but they do not reveal a callable public C++ API for opening the menu or injecting controller pointer events. Those call paths must be traced in the binary before implementing the Quest input adapter.

No replacement menu implementation belongs in this folder.
