package xyz.andoroid.airdropbot.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private long id;
    private long chatId;
    private int messagingStage = 0;
    //{number} is set when the action is done by user
    //0 - /start; 1 - main menu was sent; 2 - admin menu was sent
    //1XX - task number XX completed
    private boolean admin = false;
    private String fullName;
    private String username;
    private double balance = 0;
    private Long referer;
    private String refererName = "";
    private List<Long> referrals = new ArrayList<>();

    private Map<Integer, String> tasks = new HashMap<>();

    private String captchaSent;

    public User(long id, long chatId, String fullName, String username) {
        this.id = id;
        this.chatId = chatId;
        this.fullName = fullName;
        this.username = username;
    }

    public User setMessagingStage(int messagingStage) {
        this.messagingStage = messagingStage;
        return this;
    }

    public int getMessagingStage() {
        return messagingStage;
    }

    public long getChatId() {
        return chatId;
    }

    public User setChatId(long chatId) {
        this.chatId = chatId;
        return this;
    }

    public boolean isAdmin() {
        return admin;
    }

    public User setAdmin(boolean admin) {
        this.admin = admin;
        return this;
    }

    public long getId() {
        return id;
    }

    public String getCaptchaSent() {
        return captchaSent;
    }

    public void setCaptchaSent(String captchaSent) {
        this.captchaSent = captchaSent;
    }

    public void addTaskAnswer(int taskId, String answer) {
        tasks.put(taskId, answer);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<Long> getReferrals() {
        return referrals;
    }

    public void addReferral(long id) {
        referrals.add(id);
    }

    public void setReferer(Long referer) {
        this.referer = referer;
    }

    public Long getReferer() {
        return referer;
    }

    public void setRefererName(String refererName) {
        this.refererName = refererName;
    }

    public String getRefererName() {
        return refererName;
    }

    public void addToBalance(double amount) {
        balance += amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserAsLine() {
        StringBuilder builder = new StringBuilder((id+"")+','+username+','+fullName+','+chatId+','+messagingStage+','+admin+','+balance+','+
                referer+','+referrals.toString());
        tasks.forEach((id, ans) -> builder.append(',').append(ans));
        return builder.toString();
        //ID,username,Full Name,Chat ID,Messaging stage,Is Admin,Balance,Referer,Referrals,Tasks answers
    }

    public String getTaskAnswerById(int id) {
        return tasks.get(id);
    }
}
