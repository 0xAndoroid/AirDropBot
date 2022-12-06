package xyz.andoroid.airdropbot.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUsersDatabase implements UsersDatabase {
    private Map<Long, FileWriter> writerMap = new HashMap<>();

    @Override
    public void save(User user) {
        Gson gson = new GsonBuilder().create();
        File file = new File("users");
        file.mkdir();
        try {
            FileWriter writer;
            if(writerMap.containsKey(user.getId())) writer = writerMap.get(user.getId());
            else writer = new FileWriter("users/" + user.getId() + ".json");
            writer.write(gson.toJson(user));
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<User> loadAll() {
        File file = new File("users");
        File[] files = file.listFiles();
        Gson gson = new GsonBuilder().create();
        List<User> users = new ArrayList<>();
        if(files != null)
        for(File f : files) {
            try {
                User user = gson.fromJson(new FileReader(f), User.class);
                users.add(user);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return users;
    }
}
