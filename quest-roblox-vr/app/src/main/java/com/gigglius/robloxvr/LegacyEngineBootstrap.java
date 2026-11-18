package com.gigglius.robloxvr;

import android.content.Context;
import android.os.Build;

import java.io.File;

/**
 * Safe preflight for the recovered 2014 native client.
 *
 * This deliberately does not load proprietary native code. The recovered
 * libroblox.so is 32-bit ARM (armeabi), while Quest 3 requires 64-bit apps.
 * A native handoff must not be attempted until an arm64 engine build and the
 * exact original JNI startup contract are available.
 */
public final class LegacyEngineBootstrap {
    public static final String ENGINE_LIBRARY = "libroblox.so";
    public static final String AUDIO_LIBRARY = "libfmodex.so";
    public static final String OBB_FILE = "main.1.com.roblox.client.obb";

    private LegacyEngineBootstrap() {}

    public static String inspect(Context context) {
        StringBuilder report = new StringBuilder();
        report.append("Legacy Roblox engine preflight\n");
        report.append("Supported ABIs: ");
        report.append(join(Build.SUPPORTED_ABIS));
        report.append('\n');

        boolean arm32Supported = Build.SUPPORTED_32_BIT_ABIS != null
                && Build.SUPPORTED_32_BIT_ABIS.length > 0;
        report.append("32-bit ARM runtime: ")
                .append(arm32Supported ? "available" : "not supported")
                .append('\n');

        File nativeDir = new File(context.getApplicationInfo().nativeLibraryDir);
        File engine = new File(nativeDir, ENGINE_LIBRARY);
        File audio = new File(nativeDir, AUDIO_LIBRARY);
        report.append(ENGINE_LIBRARY).append(": ")
                .append(engine.isFile() ? engine.getAbsolutePath() : "missing")
                .append('\n');
        report.append(AUDIO_LIBRARY).append(": ")
                .append(audio.isFile() ? audio.getAbsolutePath() : "missing")
                .append('\n');

        File obb = new File(context.getObbDir(), OBB_FILE);
        report.append("Original OBB: ")
                .append(obb.isFile() ? obb.getAbsolutePath() : "missing")
                .append('\n');

        if (!arm32Supported) {
            report.append("BLOCKED: the recovered engine is 32-bit ARM, ")
                    .append("but this device only supports 64-bit app binaries. ")
                    .append("A compatible arm64 engine build is required.");
        } else if (!engine.isFile() || !audio.isFile() || !obb.isFile()) {
            report.append("BLOCKED: one or more original runtime files are missing.");
        } else {
            report.append("FILES PRESENT: native startup is still gated until ")
                    .append("the original JNI launch arguments and lifecycle ")
                    .append("are verified against the matching client sources.");
        }
        return report.toString();
    }

    private static String join(String[] values) {
        if (values == null || values.length == 0) return "(none)";
        StringBuilder result = new StringBuilder();
        for (String value : values) {
            if (result.length() > 0) result.append(", ");
            result.append(value);
        }
        return result.toString();
    }
}
