package tech.sebazcrc.permadeath.api;

public class PermadeathAPI {

    private static PermadeathAPIProvider provider;

    public static long getDay() {
        if (provider == null) {
            return -1;
        }
        return provider.getDay();
    }

    public static String getPrefix() {
        if (provider == null) {
            return "";
        }
        return provider.getPrefix();
    }

    public static boolean optifineItemsEnabled() {
        if (provider == null) {
            return false;
        }
        return provider.optifineItemsEnabled();
    }

    public static void setProvider(PermadeathAPIProvider newProvider) {
        if (provider != null) {
            // To prevent multiple initializations, although not strictly necessary.
            return;
        }
        provider = newProvider;
    }
}


