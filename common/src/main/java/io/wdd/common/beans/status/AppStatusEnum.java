package io.wdd.common.beans.status;

public enum AppStatusEnum {

    HEALTHY("Healthy", "app is running"),

    FAILURE("Failure", "app is failed"),

    NOT_INSTALL("NotInstall", "app not installed");

    String name;

    String description;

    AppStatusEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
