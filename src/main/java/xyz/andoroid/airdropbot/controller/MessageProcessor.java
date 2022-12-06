package xyz.andoroid.airdropbot.controller;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import xyz.andoroid.airdropbot.Main;
import xyz.andoroid.airdropbot.Settings;
import xyz.andoroid.airdropbot.model.*;
import xyz.andoroid.airdropbot.view.Logger;
import xyz.andoroid.airdropbot.view.Messages;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MessageProcessor {
    private Settings settings;
    private Messages messages;
    private UsersDatabase database;
    private Map<Long, User> users;
    private TelegramBot bot;
    private Groups groups;
    private List<Withdrawal> withdrawalList;

    public MessageProcessor(TelegramBot bot, Settings settings, UsersDatabase database,
                            Map<Long, User> users, Groups groups, List<Withdrawal> withdrawalList) {
        this.settings = settings;
        this.messages = new Messages(bot, settings);
        this.database = database;
        this.users = users;
        this.bot = bot;
        this.groups = groups;
        this.withdrawalList = withdrawalList;
    }

    public void processMessage(User user, Message message) {
        if(!groups.registrations && !user.isAdmin()) {
            messages.sendMessage(user, "bot_is_closed");
            return;
        }
        if(message.text().startsWith("/start")) {
            if(user.getMessagingStage() != 0) {
                messages.sendMessage(user, "err_inappropriate_start_command");
                return;
            }
            String[] splited = message.text().split(" ");
            try {
                if (splited.length == 2) {
                    long referer = Long.parseLong(splited[1]);
                    if(users.containsKey(referer)) {
                        user.setReferer(referer);
                        user.setRefererName(users.get(referer).getFullName());
                        messages.sendMessage(user, "referral_message");
                    }
                }
            } catch (NumberFormatException ex) {
                System.out.println("User with "+user.getId()+" " +user.getUsername()+" tried hacking!");
            }
            messages.sendMessage(user, "welcome_message");
            sendCaptcha(user);
        } else if(user.getMessagingStage() == 0) {
            if(message.text().equalsIgnoreCase(user.getCaptchaSent())) {
                messages.sendMessage(user, "captcha_correct");
                sendTask(user,0);
                return;
            }
            messages.sendMessage(user, "captcha_incorrect");
        } else if(user.getMessagingStage() >= 99) {
            int taskAnswer = user.getMessagingStage() - 99;
            if (settings.tasks.get(taskAnswer).check(message.text(), user.getId(), bot, groups)) {
                user.addTaskAnswer(taskAnswer, message.text());
                sendTask(user, taskAnswer + 1);
            } else {
                messages.sendMessage(user, "incorrect_task_" + taskAnswer);
            }
        } else if(message.text().equalsIgnoreCase("/menu")) {
            showMenu(user);
        } else if(user.getMessagingStage() == 3) {
            if(!groups.withdrawals) {
                showMenu(user);
            }
            try {
                double amount = Double.parseDouble(message.text());
                if(amount > user.getBalance() || amount < settings.minWithdrawal || amount <= 0) {
                    messages.sendMessage(user, "invalid_input");
                    return;
                }
                user.addToBalance(-amount);
                withdrawalList.add(new Withdrawal(user.getId(), user.getTaskAnswerById(settings.walletTaskId), amount));
                Logger.infoLog("User "+user.getId()+" has withdrawn "+amount);
                if(settings.notifyWithdrawals != 0) {
                    SendMessage sm = new SendMessage(users.get(settings.notifyWithdrawals).getChatId(),
                            "New withdrawal!\nUser: "+user.getId()+ " @" +user.getUsername()+"\n"+
                            "Amount: "+ amount +"\nWallet:\n```"+user.getTaskAnswerById(settings.walletTaskId)+"```");
                    messages.sendAsyncMessage(sm);
                }
                messages.sendMessage(user, "withdrawal_success");
                showMenu(user);

            } catch (NumberFormatException ex) {
                Logger.errorLog("NumberFormatException during withdrawal user "+user.getId());
                messages.sendMessage(user, "invalid_input");
            }
        } else if(message.text().equalsIgnoreCase("/restart")) {
            if(user.getBalance() > 0) user.addToBalance(-settings.initialValue);
            sendTask(user, 0);
        } else if(user.isAdmin() && message.text().equalsIgnoreCase("/admin")) {
            user.setMessagingStage(2);
            messages.sendMessage(user, "admin_menu");
        } else if(user.isAdmin() && user.getMessagingStage() == 2) {
            processAdminMenu(user, message);
        } else if(user.getMessagingStage() == 1) {
            processMainMenu(user, message);
        }
    }

    private void showMenu(User user) {
        user.setMessagingStage(1);
        if(user.isAdmin()) messages.sendMessage(user, "main_menu_admin");
        else messages.sendMessage(user, "main_menu");
    }

    private void processMainMenu(User user, Message message) {
        if(message.text().equals(settings.messages.get("main_menu").getKeyboard()[0][0])) {
            messages.sendMessage(user, "balance");
        } else if(message.text().equals(settings.messages.get("main_menu").getKeyboard()[0][1])) {
            messages.sendMessage(user, "referralLink");
        } else if(message.text().equals(settings.messages.get("main_menu").getKeyboard()[1][0])) {
            if(!groups.withdrawals) {
                messages.sendMessage(user, "withdrawal_closed");
                return;
            }
            messages.sendMessage(user, "withdrawal");
            user.setMessagingStage(3);
        } else if(message.text().equals(settings.messages.get("main_menu").getKeyboard()[1][1])) {
            messages.sendMessage(user, "information");
        }  else if(message.text().equals(settings.messages.get("main_menu").getKeyboard()[2][0])) {
            messages.sendMessage(user, "resubmit");
        }
    }

    private void sendCaptcha(User user) {
        if(settings.captchaEnabled) {
            messages.sendMessage(user, "please_complete_captcha_message");
            Random random = new Random();
            int f = random.nextInt(50);
            int s = random.nextInt(50);
            SendMessage message = new SendMessage(user.getChatId(), f+"+"+s);
            messages.sendAsyncMessage(message);
            user.setCaptchaSent(Integer.toString(f+s));
        } else {
            sendTask(user, 0);
        }
    }

    private void sendTask(User user, int id) {
        user.setMessagingStage(id+99);
        if(settings.tasks.size() > id)
            messages.sendMessage(user, "task_"+id);
        else {
            user.addToBalance(settings.initialValue);
            if(user.getReferer() != null) {
                users.get(user.getReferer()).addToBalance(settings.referralValue);
                messages.sendMessage(users.get(user.getReferer()), "referer_message");
                users.get(user.getReferer()).addReferral(user.getId());
                Logger.infoLog("User "+users.get(user.getReferer()).getUsername()+ " has 1 more referrals: "+user.getUsername());
            }
            showMenu(user);
        }
    }

    private void processAdminMenu(User user, Message message) {
        if(message.text().equals(settings.messages.get("admin_menu").getKeyboard()[0][0])) {
            //EXPORT AS CSV
            SendMessageTemplate sendMessageTemplate = new SendMessageTemplate("Exporting, wait...");
            messages.sendAsyncMessage(sendMessageTemplate.generateBasicSendMessage(user.getChatId()));
            try {
                FileWriter writer = new FileWriter("export.csv");
                writer.write("ID,username,Full Name,Chat ID,Messaging stage,Is Admin,Balance,Referer,Referrals,Tasks answers\n");
                users.forEach((id, u) -> {
                    try {
                        writer.append(u.getUserAsLine()).append("\n");
                    }catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            SendDocument document = new SendDocument(user.getChatId(), new File("export.csv"));
            messages.sendAsyncDocument(document);
        } else if(message.text().equals("/menu") || message.text().equals(settings.messages.get("admin_menu").getKeyboard()[1][0])) {
            showMenu(user);
        } else if(message.text().equals(settings.messages.get("admin_menu").getKeyboard()[0][1])) {
            Main.saveWithdrawals();
            SendDocument document = new SendDocument(user.getChatId(), new File("withdrawals.csv"));
            messages.sendAsyncDocument(document);
        } else if(message.text().equals(settings.messages.get("admin_menu").getKeyboard()[1][1])) {
            messages.sendMessage(user, "withdrawals_toggle");
            groups.withdrawals = !groups.withdrawals;
        }
    }
}