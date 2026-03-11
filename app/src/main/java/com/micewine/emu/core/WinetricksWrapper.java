package com.micewine.emu.core;

import static com.micewine.emu.activities.MainActivity.deviceArch;
import static com.micewine.emu.activities.MainActivity.usrDir;
import static com.micewine.emu.activities.MainActivity.winePrefix;
import static com.micewine.emu.activities.MainActivity.winePrefixesDir;
import static com.micewine.emu.core.EnvVars.getEnv;
import static com.micewine.emu.activities.MainActivity.homeDir;
import static com.micewine.emu.activities.MainActivity.wineBinDir;
import static com.micewine.emu.activities.MainActivity.wineLibDir;
import static com.micewine.emu.activities.MainActivity.box64BinDir;
import static com.micewine.emu.core.ShellLoader.runCommand;

public class WinetricksWrapper {
    private static final String IS_BOX64 = deviceArch.equals("x86_64") ? "" : "box64";

    public static void winetricks(String args) {
        winetricks(args, null);
    }

    private static String getEnv() {
        return "export LANG=pt_BR.UTF-8; " +
                "export TMPDIR=" + usrDir + "/tmp; " +
                "export HOME=" + homeDir + "; " +
                "export XDG_CONFIG_HOME=" + homeDir + "/.config; " +
                "export DISPLAY=:0; " +
                "export PULSE_LATENCY_MSEC=60; " +
                "export LD_LIBRARY_PATH=/system/lib64:" + usrDir + "/lib; " +
                "export PATH=$PATH:" + usrDir + "/bin:" + wineBinDir + ":" + wineLibDir + ":" + box64BinDir + "; " +
                "export PREFIX=" + usrDir + "; " +
                "export MESA_SHADER_CACHE_DIR=" + homeDir + "/.cache; " +
                "export MESA_VK_WSI_PRESENT_MODE=mailbox; " +
                "export MESA_GL_VERSION_OVERRIDE=3.2; " +
                "export MESA_GL_VERSION_OVERRIDE=3.2; " +
                "export MESA_GLSL_VERSION_OVERRIDE=150; " +
                "export VK_ICD_FILENAMES=" + usrDir + "/vulkan_icd.json; " +
                "export GALLIUM_DRIVER=zink; " +
                "export TU_DEBUG=noconform,sysmem; " +
                "export ZINK_DEBUG=compact; " +
                "export ZINK_DESCRIPTORS=lazy; " +
                "export BOX64_LOG=0; " +
                "export BOX64_NOBANNER=1; ";
    }

    public static void winetricks(String args, String cwd) {
        String wineWrapper = usrDir + "/bin/wine-wrapper";
        String wineserverWrapper = usrDir + "/bin/wineserver-wrapper";

        String setupWrappers = "echo '#!/bin/sh' > " + wineWrapper + "; " +
                "echo 'exec " + IS_BOX64 + " wine \"$@\"' >> " + wineWrapper + "; " +
                "echo '#!/bin/sh' > " + wineserverWrapper + "; " +
                "echo 'exec " + IS_BOX64 + " wineserver \"$@\"' >> " + wineserverWrapper + "; " +
                "chmod +x " + wineWrapper + " " + wineserverWrapper + " " + usrDir + "/bin/winetricks; ";

        String winetricksCmd = "export WINEPREFIX='" + winePrefixesDir + "/" + winePrefix + "'; " +
                "export WINE='" + wineWrapper + "'; " +
                "export WINESERVER='" + wineserverWrapper + "'; " +
                "export WINETRICKS_CHECK_FOR_UPDATES=0; " +
                "winetricks " + args;

        runCommand(getEnv() + (cwd != null ? ("cd " + cwd + "; ") : "") + setupWrappers + winetricksCmd, true);
    }
}
