package xyz.andoroid.airdropbot.model;

import java.time.LocalDateTime;

public class Withdrawal {
    public LocalDateTime time;
    public long userId;
    public String wallet;
    public double amount;

    public Withdrawal(long userId, String wallet, double amount) {
        time = LocalDateTime.now();
        this.userId = userId;
        this.amount = amount;
        this.wallet = wallet;
    }

    @Override
    public String toString() {
        return time.toString()+","+userId+","+wallet+","+amount;
    }
}
