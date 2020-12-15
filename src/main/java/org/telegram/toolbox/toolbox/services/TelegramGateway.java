package org.telegram.toolbox.toolbox.services;

import org.apache.http.util.ByteArrayBuffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.toolbox.toolbox.configuration.Settings;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class TelegramGateway extends TelegramLongPollingBot {
    private static final String HELP_COMMAND = "/help";
    private static final String CLEAR_COMMAND = "/clear";
    private static final String APPEND_COMMAND = "/append";
    private static final String PREPEND_COMMAND = "/prepend";
    private static final String SAVE_COMMAND = "/save";
    private static final String LOAD_COMMAND = "/load";
    private static final String ONE_LINE_COMMAND = "/one_line";
    private static final String RUN_COMMAND = "/run";
    private static final String STATUS_COMMAND = "/status";

    private static TelegramBotsApi telegramBotsApi;
    private final Settings settings;
    private final Core core;

    static {
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public TelegramGateway(final Settings settings, final Core core) throws TelegramApiException, IOException {
        super();
        this.settings = settings;
        this.core = core;
        telegramBotsApi.registerBot(this);
    }

    public void sendResult(final String id, final String output) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(id);
        sendMessage.setText("Result: " + output);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String sendFile(final String id, final String label, final InputStream is) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setDocument(new InputFile(is, label));
        sendDocument.setChatId(id);

        try {
            Message response = execute(sendDocument);
            return response.getDocument().getFileId();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getFileAsString(final String fileId) {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        try {
            InputStream in = downloadFileAsStream(execute(getFile));
            ByteArrayBuffer baf = new ByteArrayBuffer(32);
            BufferedInputStream bis = new BufferedInputStream(in);

            int buffer;
            while ((buffer = bis.read()) != -1) {
                baf.append((byte) buffer);
            }

            bis.close();
            in.close();
            return new String(baf.toByteArray());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ReplyKeyboardMarkup getKeyBoard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> arr = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(HELP_COMMAND);
        keyboardRow.add(RUN_COMMAND);
        keyboardRow.add(CLEAR_COMMAND);
        keyboardRow.add(STATUS_COMMAND);
        arr.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(arr);
        return replyKeyboardMarkup;
    }

    public void sendGreetMessage(final String id) {
        SendMessage statusMessage = new SendMessage();
        statusMessage.setChatId(id);
        statusMessage.setText("Hi. This is simple always-with-you python interpreter. \n" +
                "Here you can store and run python 3 scripts for any purpose.\nClick help to learn more about api");

        statusMessage.setReplyMarkup(getKeyBoard());
        try {
            execute(statusMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendHelpMessage(final String id) {
        SendMessage statusMessage = new SendMessage();
        statusMessage.setChatId(id);
        statusMessage.setText("Anytime you have a state. State - is a workbook where you can add loaded scripts or completely clear it." +
                "\n Here you have 5 commands. \n /clear - clear workbook\n /run - run created script\n /save NAME - save under NAME" +
                "\n /append NAME - add script with NAME at the bottom of workbook \n /prepend NAME - like append but at the top" +
                "\n /status - show current workbook\n /load LABEL - ready file script loading");

        statusMessage.setReplyMarkup(getKeyBoard());
        try {
            execute(statusMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendStatusMessage(final String id, final String code) {
        SendMessage statusMessage = new SendMessage();
        statusMessage.setChatId(id);
        statusMessage.setText("Workbook:\n" + code);

        statusMessage.setReplyMarkup(getKeyBoard());
        try {
            execute(statusMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return settings.getBotName();
    }

    @Override
    public String getBotToken() {
        return settings.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("RECEIVED UPDATE " + update.getUpdateId());
        if (update.hasMessage()) {
            Message message = update.getMessage();
            String id = message.getChatId().toString();

            if (message.hasDocument()) {
                Document document = message.getDocument();
                String caption = message.getCaption();

                if (LOAD_COMMAND.equals(caption.substring(0, LOAD_COMMAND.length()))) {
                    String label = caption.substring(LOAD_COMMAND.length());
                    core.load(id, label, document.getFileId());
                }

                return;
            }

            String text = message.getText();
            String entity = message.getEntities().get(0).getText();

            if (CLEAR_COMMAND.equals(entity)) {
                core.clear(id);
            }

            if (APPEND_COMMAND.equals(entity)) {
                String fileLabel = entity.length() == text.length() ? "" : text.substring(APPEND_COMMAND.length() + 1);
                core.append(id, fileLabel);
            }

            if (PREPEND_COMMAND.equals(entity)) {
                String fileLabel = entity.length() == text.length() ? "" : text.substring(PREPEND_COMMAND.length() + 1);
                core.prepend(id, fileLabel);
            }

            if (SAVE_COMMAND.equals(entity)) {
                String fileLabel = entity.length() == text.length() ? "" : text.substring(SAVE_COMMAND.length() + 1);
                core.save(id, fileLabel);
            }

            if (ONE_LINE_COMMAND.equals(entity)) {
                String code = entity.length() == text.length() ? "" : text.substring(ONE_LINE_COMMAND.length() + 1);
                core.appendLine(id, code);
            }

            if (RUN_COMMAND.equals(entity)) {
                String input = entity.length() == text.length() ? "" : text.substring(RUN_COMMAND.length() + 1);
                core.run(id, input);
            }

            if (HELP_COMMAND.equals(entity)) {
                sendHelpMessage(id);
            }

            if (STATUS_COMMAND.equals(entity)) {
                core.status(id);
            }
        }
    }
}
