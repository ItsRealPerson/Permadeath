package dev.itsrealperson.permadeath.util;

import lombok.Getter;
import org.bukkit.Bukkit;

public class VersionManager {
    @Getter
    private static final String version;
    @Getter
    private static MinecraftVersion minecraftVersion;

    static {
        String bukkitVersion = Bukkit.getBukkitVersion();
        MinecraftVersion found = null;

        if (bukkitVersion.contains("1.21")) {
            found = MinecraftVersion.v1_21_R3;
        } else if (bukkitVersion.contains("1.20")) {
            found = MinecraftVersion.v1_20_R1;
        } else if (bukkitVersion.contains("1.16")) {
            found = MinecraftVersion.v1_16_R3;
        } else if (bukkitVersion.contains("1.15")) {
            found = MinecraftVersion.v1_15_R1;
        }

        if (found == null) {
            try {
                String packageName = Bukkit.getServer().getClass().getPackage().getName();
                String[] parts = packageName.split("\\.");
                if (parts.length > 3) {
                    String rev = parts[3].substring(1);
                    found = MinecraftVersion.valueOf("v" + rev);
                }
            } catch (Exception ignored) {
            }
        }

        minecraftVersion = found;
        version = (found != null) ? found.name().substring(1) : "unknown";
    }

    public static String getRev() {
        return getVersion();
    }

    public static boolean isValidVersionSet() {
        return minecraftVersion != null;
    }

    public static String getFormattedVersion() {
        return minecraftVersion.getFormattedName();
    }

    public static boolean isRunningPostNetherUpdate() {
        return minecraftVersion != MinecraftVersion.v1_15_R1;
    }

    public static boolean isRunningPostAncientUpdate(){ return minecraftVersion != MinecraftVersion.v1_15_R1 && minecraftVersion != MinecraftVersion.v1_16_R3;}

}


