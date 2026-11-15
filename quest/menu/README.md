# Quest 3 menu foundation

First platform-independent menu implementation for the Roblox 2014 Quest port.

## Menu screens
- Main: Play, Avatar, Settings, Quit
- Settings: comfort and turning option entry points, Back
- Avatar: default-avatar entry point, Back
- Controller pointer hover and trigger edge detection

## Interaction coordinates
The menu is designed as a world-space panel. Its local coordinates are normalized to -0.5 through +0.5 on each axis. QuestMenu performs button hit testing and detects trigger presses on the down edge.

An OpenXR input adapter should read the right controller aim pose, transform its ray into panel-local space, intersect the panel plane, then pass the local x/y and trigger state to updatePointer(). The renderer should draw buttons() and highlight hoveredButton().

## Integration status
This is the menu state and interaction foundation, not yet a rendered in-headset panel. Rendering must be connected to the engine UI/render path after the graphics-context integration is established. StartGame should invoke the preserved original place-launch flow. Quit should be mapped to the Android activity lifecycle, not native process termination.

## Build
Add QuestMenu.cpp to the native target that owns the Quest client. The implementation is C++11 and has no OpenXR or Android header dependency.
