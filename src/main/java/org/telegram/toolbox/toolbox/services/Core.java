package org.telegram.toolbox.toolbox.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.toolbox.toolbox.Utils;
import org.telegram.toolbox.toolbox.models.Source;
import org.telegram.toolbox.toolbox.models.User;
import org.telegram.toolbox.toolbox.models.UserState;
import org.telegram.toolbox.toolbox.runners.PythonRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class Core {
    private final DatabaseGateway databaseGateway;
    private final Map<String, UserState> userStateMap = new HashMap<>();
    private final PythonRunner pythonRunner = new PythonRunner();
    @Autowired
    private TelegramGateway telegramGateway;

    @Autowired
    public Core(final DatabaseGateway databaseGateway) {
        this.databaseGateway = databaseGateway;
    }


    public void status(final String id) {
        UserState userState = getUserState(id);
        telegramGateway.sendStatusMessage(id, userState.getCode().toString());
    }

    public void clear(final String id) {
        UserState userState = getUserState(id);
        userState.getCode().setLength(0);
    }

    public void append(final String id, final String fileLabel) {
        UserState userState = getUserState(id);
        Source source = databaseGateway.getSource(id, fileLabel);

        userState.getCode().append(telegramGateway.getFileAsString(source.getFileId()));
        userState.getCode().append('\n');
    }

    public void prepend(final String id, final String fileLabel) {
        UserState userState = getUserState(id);
        Source source = databaseGateway.getSource(id, fileLabel);

        userState.getCode().append('\n');
        userState.getCode().insert(0, telegramGateway.getFileAsString(source.getFileId()));
    }

    public void appendLine(final String id, final String line) {
        UserState userState = getUserState(id);
        userState.getCode().append(line);
        userState.getCode().append('\n');
    }

    public void save(final String id, final String label) {
        UserState userState = getUserState(id);
        String fileId = telegramGateway.sendFile(id, label, new ByteArrayInputStream(
                userState.getCode().toString().getBytes((StandardCharsets.UTF_8))));

        Source source = new Source().setAuthor(id).setFileId(fileId).setAccess(Source.Access.PRIVATE).setType("python").setLabel(label);
        databaseGateway.insert(source);
    }

    public void load(final String id, final String label, final String fileId) {
        UserState userState = getUserState(id);

        Source source = new Source().setAuthor(id).setFileId(fileId).setAccess(Source.Access.PRIVATE).setType("python").setLabel(label);
        databaseGateway.insert(source);
    }

    public void run(final String id, final String input) {
        UserState userState = getUserState(id);

        try {
            telegramGateway.sendResult(id, pythonRunner.execute(userState.getCode().toString(), input));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private UserState getUserState(final String id) {
        UserState userState = userStateMap.get(id);
        if (userState == null) {
            telegramGateway.sendGreetMessage(id);
            User user = databaseGateway.getUser(id);
            if (user == null) {
                user = new User().setId(id).setCarma(0);
                databaseGateway.insertOrUpdate(user);
            }

            userState = new UserState().setCarma(user.getCarma());

            userStateMap.put(id, userState);
        }

        return userState;
    }
}
