package com.micewine.emu.core;

import static com.micewine.emu.activities.MainActivity.deviceArch;
import static com.micewine.emu.activities.MainActivity.usrDir;
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
        String wineWrapper = usrDir + "/bin/wine-wrapper";
        String wineserverWrapper = usrDir + "/bin/wineserver-wrapper";

        String setupWrappers = "echo '#!/bin/sh\\n" + IS_BOX64 + " wine \"$@\"' > " + wineWrapper + "; " +
                "echo '#!/bin/sh\\n" + IS_BOX64 + " wineserver \"$@\"' > " + wineserverWrapper + "; " +
                "chmod +x " + wineWrapper + " " + wineserverWrapper + "; ";

        runCommand(((cwd != null) ? "cd " + cwd + ";" : "") + getEnv() +
                setupWrappers +
                "WINEPREFIX='" + winePrefixesDir + "/" + winePrefix + "' " +
                "WINE='" + wineWrapper + "' " +
                "WINESERVER='" + wineserverWrapper + "' " +
                "winetricks " + args, true);
    }
}
