package xyz.andoroid.airdropbot.model;

import java.util.List;

public interface UsersDatabase {
    void save(User user);

    List<User> loadAll();
}
