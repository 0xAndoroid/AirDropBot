package xyz.andoroid.airdropbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import xyz.andoroid.airdropbot.controller.MessageProcessor;
import xyz.andoroid.airdropbot.model.*;
import xyz.andoroid.airdropbot.view.Logger;

import java.io.*;
import java.util.*;

public class Main {
    public static String BOT_LINK;
    private static List<Withdrawal> withdrawals = new ArrayList<>();

    public static void main(String[] args) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Settings settings;
        Groups groups;
        try {
            Logger.writer = new FileWriter("log.txt",true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            settings = gson.fromJson(new FileReader("settings.json"), Settings.class);
        } catch (FileNotFoundException ex) {
            settings = new Settings();
            try {
                FileWriter writer = new FileWriter("settings.json");
                writer.write(gson.toJson(settings));
                writer.close();
                System.out.println("Sample File created");
            } catch (IOException e) {
                System.out.println("IOException");
                return;
            }
        }
        try {
            groups = gson.fromJson(new FileReader("groups.json"), Groups.class);
        } catch (FileNotFoundException ex) {
            groups = new Groups();
        }
        BOT_LINK = settings.botLink;
        UsersDatabase database = new JsonUsersDatabase();
        List<User> userList = database.loadAll();
        Map<Long, User> users = new HashMap<>();
        for(User u : userList) users.put(u.getId(), u);
        TelegramBot bot = new TelegramBot(settings.botToken);
        final Groups finalGroups = groups;
        MessageProcessor messageProcessor = new MessageProcessor(bot, settings, database, users, finalGroups, withdrawals);
        Thread saving = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                save(gson, database, users, finalGroups);
            }
        });
        saving.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            save(gson, database, users, finalGroups);
        }, "Shutdown-thread"));
        Thread command = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            do {
                String line = scanner.nextLine();
                if(line.equalsIgnoreCase("stop")) {
                    System.exit(0);
                } else if(line.equalsIgnoreCase("save")) {
                    save(gson, database, users, finalGroups);
                }
            } while (true);
        });
        command.start();
        Logger.infoLog("Starting the bot!");
        bot.setUpdatesListener(updates -> {
            for(Update update : updates) {
                if(update.message() == null) continue;
                if(update.message().chat() == null) continue;
                if(update.message().chat().type() != Chat.Type.Private) {
                    if(finalGroups.groups.contains(update.message().chat().id())) continue;
                    SendMessage message = new SendMessage(update.message().chat().id(), "Group Chat ID: "+update.message().chat().id());
                    bot.execute(message);
                    finalGroups.groups.add(update.message().chat().id());
                    continue;
                }
                if(update.message().text() == null || update.message().text().equalsIgnoreCase("")) continue;
                User user;
                if(users.containsKey(update.message().from().id()))
                    user = users.get(update.message().from().id());
                else {
                    String fn = update.message().from().firstName();
                    String ln = update.message().from().lastName();
                    user = new User(update.message().from().id(), update.message().chat().id(),
                            (fn==null?"":fn+" ")+(ln==null?"":ln),
                            update.message().from().username());
                    users.put(update.message().from().id(), user);
                    Logger.infoLog("New user @"+user.getUsername() + " " + user.getId());
                    database.save(user);
                }
                messageProcessor.processMessage(user, update.message());
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private static void save(Gson gson, UsersDatabase database, Map<Long, User> users, Groups finalGroups) {
        Logger.infoLog("Saving the state and closing.");
        try {
            Logger.writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        users.forEach((aLong, user) -> database.save(user));
        saveWithdrawals();
        try {
            FileWriter writer = new FileWriter("groups.json");
            writer.write(gson.toJson(finalGroups));
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void saveWithdrawals() {
        try {
            File file = new File("withdrawals.csv");
            boolean append = !file.exists();
            FileWriter writer = new FileWriter(file,true);
            if(append) writer.append("Time,User id,Wallet,Amount\n");
            for(Withdrawal w : withdrawals) {
                writer.append(w.toString()).append("\n");
            }
            withdrawals.clear();
            writer.close();
        } catch (IOException ex) {
            Logger.errorLog("IOException during saving withdrawal");
        }
    }
}
