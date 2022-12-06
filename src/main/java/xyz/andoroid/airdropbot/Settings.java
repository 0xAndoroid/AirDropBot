package xyz.andoroid.airdropbot;

import xyz.andoroid.airdropbot.model.SendMessageTemplate;
import xyz.andoroid.airdropbot.model.Task;

import java.util.*;

public class Settings {
    protected String botToken;
    public String botLink;
    public String tokenName;
    public String tokenSymbol;
    public double initialValue;
    public double referralValue;
    public int walletTaskId = 0;
    public long notifyWithdrawals = 0;
    public boolean captchaEnabled;
    public double minWithdrawal = 0;

    public List<Task> tasks = new ArrayList<>();
    public Map<String, SendMessageTemplate> messages = new HashMap<>();

    public Settings() {
        botToken = "BOT TOKEN";
        tokenName = "TOKEN NAME";
        tokenSymbol = "TOKEN SYMBOL";
        initialValue = 0;
        referralValue = 0;
        captchaEnabled = false;
        Task task = new Task();
        tasks.add(task);
        SendMessageTemplate sendMessageTemplate = new SendMessageTemplate("TEXT");
        sendMessageTemplate.init();
        messages.put("SAMPLE_MSG", sendMessageTemplate);
    }
}
