package command;

import conf.Config;
import data.Storage;
import data.StorageCleanUpTask;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class CommandResponce {
  private final OutputStream outSocket;
  private final Config config;
  private final CommandBuilder commandBuilder;

  public CommandResponce(OutputStream outSocket, Config config, CommandBuilder commandBuilder) {
    this.outSocket = outSocket;
    this.config = config;
    this.commandBuilder = commandBuilder;
  }

  public void handleResponce(ArrayList<String> command) throws IOException {
    if (command.size() == 0) {
      return;
    }

    switch (command.getFirst().toUpperCase()) {
      case "COMMAND":
        send(commandBuilder.buildList(Collections.emptyList()));
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

      case "KEYS":
        send(commandBuilder.buildList(Storage.getKeys()));
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

      case "INFO":
        Map<String, String> info = new HashMap<>();
        info.put("role", "master");
        StringBuilder infoResponce = new StringBuilder();

        for (var entry : info.entrySet()) {
          infoResponce.append(entry.getKey() + ":" + entry.getValue());
        }

        String infoResponceMsg = infoResponce.toString();
        send("$" + String.valueOf(infoResponceMsg.length()) + "\r\n" + infoResponceMsg + "\r\n");

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
