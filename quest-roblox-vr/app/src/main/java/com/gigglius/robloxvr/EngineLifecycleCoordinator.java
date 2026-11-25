package com.gigglius.robloxvr;

/**
 * Evidence-gated lifecycle coordinator for the legacy engine port.
 *
 * This is intentionally Android-independent and does not call native code.
 * Startup remains blocked until an ARM64 engine and verified launch contract
 * are explicitly supplied by future integration code.
 */
public final class EngineLifecycleCoordinator {
    public enum State {
        COLD, APP_READY, SURFACE_READY, ASSETS_READY,
        ENGINE_COMPATIBLE, CONTRACT_VERIFIED, RUNNING, PAUSED, STOPPED, FAILED
    }

    private State state = State.COLD;
    private boolean surfaceAvailable;
    private boolean assetsReady;
    private boolean arm64EngineAvailable;
    private boolean launchContractVerified;
    private String lastReason = "Not initialized";

    public synchronized State getState() { return state; }
    public synchronized String getLastReason() { return lastReason; }

    public synchronized void appCreated() {
        state = State.APP_READY;
        lastReason = "Android host created; native engine is not loaded.";
    }

    public synchronized void surfaceCreated() {
        surfaceAvailable = true;
        if (state != State.RUNNING) state = State.SURFACE_READY;
        lastReason = "Presentation surface available; no renderer attached.";
    }

    public synchronized void surfaceDestroyed() {
        surfaceAvailable = false;
        if (state == State.RUNNING || state == State.PAUSED) state = State.STOPPED;
        lastReason = "Surface destroyed; engine start remains gated.";
    }

    public synchronized void assetsVerified() {
        assetsReady = true;
        state = State.ASSETS_READY;
        lastReason = "Asset readiness asserted by caller.";
    }

    public synchronized void arm64EngineVerified() {
        arm64EngineAvailable = true;
        state = State.ENGINE_COMPATIBLE;
        lastReason = "ARM64 engine compatibility asserted by caller.";
    }

    public synchronized void launchContractVerified() {
        launchContractVerified = true;
        state = State.CONTRACT_VERIFIED;
        lastReason = "JNI contract and startup arguments asserted verified.";
    }

    /** Returns false and records the exact missing prerequisite; never launches JNI. */
    public synchronized boolean requestStart() {
        if (!surfaceAvailable) return blocked("No Android surface.");
        if (!assetsReady) return blocked("Assets/OBB not verified.");
        if (!arm64EngineAvailable) return blocked("No verified ARM64 engine build.");
        if (!launchContractVerified) return blocked("Original JNI launch contract not verified.");
        lastReason = "All gates passed, but no native launch implementation is connected.";
        state = State.CONTRACT_VERIFIED;
        return false;
    }

    public synchronized void pause() {
        if (state == State.RUNNING) state = State.PAUSED;
        lastReason = "Host paused; native pause callback is not connected.";
    }

    public synchronized void fail(String detail) {
        state = State.FAILED;
        lastReason = detail == null ? "Unspecified failure." : detail;
    }

    private boolean blocked(String reason) {
        lastReason = "START BLOCKED: " + reason;
        return false;
    }
}
