package cz.cervenka.p2p_project.config;

public class ConfigTimeout {

    private static int commandTimeout = ApplicationConfig.getInt("client.commandTimeout");
    private static int userTimeout = ApplicationConfig.getInt("client.readTimeout");

    /**
     * Retrieves a value of timeout used when processing commands.
     * @return Integer value of timeout.
     */
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