package org.telegram.toolbox.toolbox.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "settings")
public class Settings {
    private String botName;
    private String token;
    private String dbPath;
    private String dbUser;
    private String dbPassword;

    public String getBotName() {
        return botName;
    }

    public Settings setBotName(String botName) {
        this.botName = botName;
        return this;
    }

    public String getToken() {
        return token;
    }

    public Settings setToken(String token) {
        this.token = token;
        return this;
    }

    public String getDbPath() {
        return dbPath;
    }

    public Settings setDbPath(String dbPath) {
        this.dbPath = dbPath;
        return this;
    }

    public String getDbUser() {
        return dbUser;
    }

    public Settings setDbUser(String dbUser) {
        this.dbUser = dbUser;
        return this;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public Settings setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
        return this;
    }
}
