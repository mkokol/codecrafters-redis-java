package command;

import conf.Config;
import data.Storage;
import data.StorageCleanUpTask;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class CommandResponce {
  private final OutputStream outSocket;
  private final Config config;

  public CommandResponce(OutputStream outSocket, Config config) {
    this.outSocket = outSocket;
    this.config = config;
  }

  public void handleResponce(ArrayList<String> command) throws IOException {
    if (command.size() == 0) {
      return;
    }

    switch (command.getFirst().toUpperCase()) {
      case "COMMAND":
        send("*0\r\n");
        break;

      case "PING":
        send("+PONG\r\n");
        break;

      case "ECHO":
        send("+" + command.get(1) + "\r\n");
        break;

      case "SET":
        Storage.set(command.get(1), command.get(2));

        if (command.size() >= 5 && command.get(3).toUpperCase().equals("PX")) {
          (new Timer())
              .schedule(new StorageCleanUpTask(command.get(1)), Integer.parseInt(command.get(4)));
        }

        send("+OK\r\n");
        break;

      case "GET":
        String val = Storage.get(command.get(1));

        if (val != null) {
          send("$" + val.length() + "\r\n" + val + "\r\n");
        } else {
          send("$-1\r\n");
        }
        break;

      case "CONFIG":
        if (command.get(1).toUpperCase().equals("GET")) {
          if (command.get(2).toLowerCase().equals("dir")) {
            send(
                String.format(
                    "*2\r\n$3\r\ndir\r\n$%d\r\n%s\r\n",
                    config.getRdbDir().length(), config.getRdbDir()));
          }

          if (command.get(2).toLowerCase().equals("dbfilename")) {
            send(
                String.format(
                    "*2\r\n$10\r\ndbfilename\r\n$%d\r\n%s\r\n",
                    config.getRdbFileName().length(), config.getRdbFileName()));
          }
        }
        break;

      case "KEYS":
        List<String> keys = Storage.getKeys();
        StringBuilder responce = new StringBuilder("*" + String.valueOf(keys.size()) + "\r\n");

        for (String key : keys) {
          responce.append("$" + String.valueOf(key.length()) + "\r\n" + key + "\r\n");
        }

        send(responce.toString());
        break;

      default:
        System.out.println(
            String.format("Commang: '%s' is not implemented.", command.getFirst().toUpperCase()));
    }
  }

  public void send(String message) throws IOException {
    outSocket.write(message.getBytes());
  }
}
