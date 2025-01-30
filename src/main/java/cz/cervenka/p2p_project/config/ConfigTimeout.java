package cz.cervenka.p2p_project.config;

public class ConfigTimeout {

    private static int commandTimeout = 5000;
    private static int userTimeout = 30000;

    public static int getCommandTimeout() {
        return commandTimeout;
    }

    public static void setCommandTimeout(int timeout) {
        commandTimeout = timeout;
    }

    public static int getUserTimeout() {
        return userTimeout;
    }

    public static void setUserTimeout(int userTimeout) {
        ConfigTimeout.userTimeout = userTimeout;
    }
}