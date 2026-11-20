package com.gigglius.robloxvr;

import android.content.Context;
import android.os.Build;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * Runtime preflight for the private Quest 3 port.
 *
 * The recovered 2014 client ships ARM32-only native libraries. Quest 3 uses
 * ARM64 native app binaries, so the old libraries are deliberately not loaded.
 * This gate is removed only after compatible arm64 engine and FMOD builds exist.
 */
public final class LegacyEngineBootstrap {
    public static final String ENGINE_LIBRARY = "libroblox.so";
    public static final String AUDIO_LIBRARY = "libfmodex.so";
    public static final String OBB_FILE = "main.1.com.roblox.client.obb";
    private static final String EXPECTED_OBB_MD5 =
            "9e11c2d97cc194d1516cbac0dfc133de";

    private LegacyEngineBootstrap() {}

    public static String inspect(Context context) {
        StringBuilder report = new StringBuilder();
        report.append("Roblox 2014 / Quest 3 preflight\n");
        report.append("Device ABIs: ").append(join(Build.SUPPORTED_ABIS)).append('\n');

        boolean arm64 = contains(Build.SUPPORTED_ABIS, "arm64-v8a");
        report.append("ARM64 runtime: ").append(arm64 ? "available" : "not available").append('\n');

        File nativeDir = new File(context.getApplicationInfo().nativeLibraryDir);
        File engine = new File(nativeDir, ENGINE_LIBRARY);
        File audio = new File(nativeDir, AUDIO_LIBRARY);
        report.append("ARM64 ").append(ENGINE_LIBRARY).append(": ")
                .append(engine.isFile() ? "present" : "missing").append('\n');
        report.append("ARM64 ").append(AUDIO_LIBRARY).append(": ")
                .append(audio.isFile() ? "present" : "missing").append('\n');

        File obb = new File(context.getObbDir(), OBB_FILE);
        report.append("Original OBB: ").append(obb.isFile() ? "present" : "missing").append('\n');
        if (obb.isFile()) {
            String actual = md5(obb);
            report.append("OBB MD5: ").append(actual == null ? "could not calculate" : actual).append('\n');
            report.append("OBB fingerprint: ")
                    .append(EXPECTED_OBB_MD5.equals(actual) ? "matches recovered APK" : "MISMATCH")
                    .append('\n');
        }

        if (!arm64) {
            report.append("BLOCKED: this build requires an ARM64 Quest runtime.");
        } else if (!engine.isFile() || !audio.isFile()) {
            report.append("BLOCKED: no ARM64 Roblox engine/FM​​OD pair is packaged. ")
                    .append("The recovered APK contains ARM32 binaries only; they cannot be loaded here.");
        } else if (!obb.isFile() || !EXPECTED_OBB_MD5.equals(md5(obb))) {
            report.append("BLOCKED: the original expansion archive is missing or does not match.");
        } else {
            report.append("FILES READY: JNI startup remains disabled until the ARM64 port's ")
                    .append("native entry points and Android surface lifecycle are implemented.");
        }
        return report.toString();
    }

    private static boolean contains(String[] values, String expected) {
        if (values == null) return false;
        for (String value : values) if (expected.equals(value)) return true;
        return false;
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

    private static String md5(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[32 * 1024];
            try (FileInputStream input = new FileInputStream(file)) {
                int count;
                while ((count = input.read(buffer)) != -1) digest.update(buffer, 0, count);
            }
            StringBuilder hex = new StringBuilder();
            for (byte value : digest.digest()) hex.append(String.format("%02x", value & 0xff));
            return hex.toString();
        } catch (Exception ignored) {
            return null;
        }
    }
}
