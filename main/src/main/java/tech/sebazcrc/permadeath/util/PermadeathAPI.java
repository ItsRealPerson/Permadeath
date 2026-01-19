package tech.sebazcrc.permadeath.util;

import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.data.DateManager;

public class PermadeathAPI {
    public static long getDay() {
        return DateManager.getInstance().getDay();
    }

    public static boolean optifineItemsEnabled() {
        return Main.optifineItemsEnabled();
    }

    public static String getPrefix() {
        return Main.prefix;
    }
}
