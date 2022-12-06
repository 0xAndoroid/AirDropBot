package xyz.andoroid.airdropbot.view;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import xyz.andoroid.airdropbot.Settings;
import xyz.andoroid.airdropbot.model.User;

import java.io.IOException;

public class Messages {
    private TelegramBot bot;
    private Settings settings;

    public Messages(TelegramBot bot, Settings settings) {
        this.bot = bot;
        this.settings = settings;
    }

    public void sendMessage(User user, String messageId) {
        sendAsyncMessage(settings.messages.get(messageId).generateAdvancedSentMessage(user));
    }

    public void sendAsyncMessage(SendMessage sendMessage) {
        bot.execute(sendMessage, new Callback<SendMessage, SendResponse>() {
            @Override
            public void onResponse(SendMessage sendMessage, SendResponse sendResponse) {

            }

            @Override
            public void onFailure(SendMessage sendMessage, IOException e) {
                Logger.errorLog("Failure of sending the message.");
                e.printStackTrace();
            }
        });
    }

    public void sendAsyncDocument(SendDocument sendMessage) {
        bot.execute(sendMessage, new Callback<SendDocument, SendResponse>() {
            @Override
            public void onResponse(SendDocument sendMessage, SendResponse sendResponse) {

            }

            @Override
            public void onFailure(SendDocument sendMessage, IOException e) {
                Logger.errorLog("Failure of sending the exported document.");
                e.printStackTrace();
            }
        });
    }
}
