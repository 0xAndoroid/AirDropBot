package xyz.andoroid.airdropbot.model;

import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import xyz.andoroid.airdropbot.Main;

import java.util.ArrayList;
import java.util.List;

public class SendMessageTemplate {
    private List<MessageEntity> entities = new ArrayList<>();
    private String[][] keyboard;
    private boolean useKeyboardRemove = true;
    private ParseMode parseMode = ParseMode.HTML;
    private Boolean disableWebPagePreview = true;
    private String text;
    private String id;
    private Boolean disableNotification = false;
    private Integer replyToMessageId;
    private Boolean allowSendingWithoutReply = false;

    public SendMessageTemplate(String text) {
        this.text = text;
    }

    public SendMessage generateBasicSendMessage(long chatId) {
        return new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(false)
                .disableNotification(false)
                .allowSendingWithoutReply(false);
    }

    public SendMessage generateAdvancedSentMessage(User user) {
        SendMessage sendMessage = new SendMessage(user.getChatId(), text
                .replace("%fullname%", user.getFullName())
                .replace("%balance%",user.getBalance()+"")
                .replace("%link%", Main.BOT_LINK+"?start="+user.getId())
                .replace("%numberOfReferrals%", user.getReferrals().size()+"")
                .replace("%referer%", user.getRefererName()));
        if(parseMode != null) sendMessage.parseMode(parseMode);
        if(keyboard != null) sendMessage.replyMarkup(new ReplyKeyboardMarkup(keyboard, true, false, true));
        else if(useKeyboardRemove) sendMessage.replyMarkup(new ReplyKeyboardRemove());
        if(!entities.isEmpty()) sendMessage.entities(entities.toArray(MessageEntity[]::new));
        if(disableWebPagePreview != null) sendMessage.disableWebPagePreview(disableWebPagePreview);
        if(disableNotification != null) sendMessage.disableNotification(disableNotification);
        if(replyToMessageId != null) sendMessage.replyToMessageId(replyToMessageId);
        if(allowSendingWithoutReply != null) sendMessage.allowSendingWithoutReply(allowSendingWithoutReply);
        return sendMessage;
    }

    public void init() {
        MessageEntity entity = new MessageEntity(MessageEntity.Type.url, 1, 5).url("test.com");
        entities.add(entity);
        keyboard = new String[][]{{"Test1", "Test2"}, {"Test3", "Test4"}};
        replyToMessageId = 0;
    }

    public String[][] getKeyboard() {
        return keyboard;
    }
}
