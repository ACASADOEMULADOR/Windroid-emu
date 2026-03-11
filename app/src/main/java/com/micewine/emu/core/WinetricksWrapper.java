package com.micewine.emu.core;

import static com.micewine.emu.activities.MainActivity.deviceArch;
import static com.micewine.emu.activities.MainActivity.winePrefix;
import static com.micewine.emu.activities.MainActivity.winePrefixesDir;
import static com.micewine.emu.core.EnvVars.getEnv;
import static com.micewine.emu.core.ShellLoader.runCommand;

public class WinetricksWrapper {
    private static final String IS_BOX64 = deviceArch.equals("x86_64") ? "" : "box64";

    public static void winetricks(String args) {
        winetricks(args, null);
    }

    public static void winetricks(String args, String cwd) {
        runCommand(((cwd != null) ? "cd " + cwd + ";" : "") + getEnv() + 
                   "WINEPREFIX='" + winePrefixesDir + "/" + winePrefix + "' " + 
                   IS_BOX64 + " winetricks " + args, true);
    }
}
