package xyz.andoroid.airdropbot.view;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class Logger {
    public static FileWriter writer;

    public static void infoLog(String msg) {
        System.out.println("["+LocalDateTime.now().toString()+"] (INFO) "+msg);
        try {
            writer.write("[" + LocalDateTime.now().toString() + "] (INFO) " + msg + "\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void errorLog(String msg) {
        System.out.println("["+LocalDateTime.now().toString()+"] (ERR) "+msg);
        try {
            writer.write("["+LocalDateTime.now().toString()+"] (ERR) "+msg);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
