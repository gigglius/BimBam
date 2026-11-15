package com.roblox.client;

import android.view.Surface;
import android.view.SurfaceHolder;

/**
 * JNI declaration bridge for the 2014 Roblox Android native library.
 *
 * The method names and parameter types mirror the declarations recovered
 * from the original ActivityGlView.smali. This class deliberately does not
 * load or invoke the native libraries yet: the recovered engine is 32-bit
 * ARM and Quest 3 ABI compatibility has not been established.
 *
 * This is not a replacement for the original ActivityGlView implementation.
 */
public class ActivityGlView extends RobloxServiceActivity
        implements SurfaceHolder.Callback {

    private static native void nativeCallMessagesFromMainThread();
    private static native void nativeHandleBackPressed();
    private static native void nativeInGamePurchaseFinished(
            boolean success, long productId, String receipt);
    private static native void nativeOnLowMemory();
    private static native void nativePassText(long textFieldId, String text);
    private static native void nativeReleaseFocus(long textFieldId);
    private static native void nativeShutDownGraphics(Surface surface);
    private static native void nativeStartGame(
            Surface surface,
            String firstArgument,
            String secondArgument,
            float density,
            boolean flag);
    private static native void nativeStartUpGraphics(Surface surface, float density);
    private static native void nativeStopGame();
    private static native void nativeVideoAdFinished(boolean finished);

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Native startup is intentionally disabled until ABI compatibility
        // and the original launch arguments have been verified.
    }

    @Override
    public void surfaceChanged(
            SurfaceHolder holder, int format, int width, int height) {
        // The original renderer's surface-change behavior still needs review.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Do not call native shutdown until the original lifecycle is restored.
    }
}
