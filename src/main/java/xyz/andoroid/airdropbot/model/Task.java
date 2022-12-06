package xyz.andoroid.airdropbot.model;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.response.GetChatMemberResponse;

public class Task {
    private int id;
    private String regex;
    private boolean noDuplicates = false;
    private boolean checkTelegram = false;
    private long checkTelegramGroupId;

    public Task() {
        id = 0;
        regex = "REGEX";
    }

    public boolean check(String msg, long userId, TelegramBot bot, Groups groups) {
        if(!checkTelegram)
            return msg.matches(regex);
        else {
            GetChatMember getChatMember = new GetChatMember(checkTelegramGroupId, userId);
            GetChatMemberResponse resp = bot.execute(getChatMember);
            System.out.println(resp);
            return ((resp.chatMember() != null) || !groups.groups.contains(checkTelegramGroupId)) && msg.matches(regex);
        }
    }
}
