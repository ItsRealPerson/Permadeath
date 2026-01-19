package tech.sebazcrc.permadeath.api;

public interface PermadeathAPIProvider {

    long getDay();

    boolean isOptifineEnabled();

    String getPrefix();

    boolean isExtendedDifficulty();
}
