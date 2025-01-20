package command;

import conf.Config;
import data.Storage;
import data.StorageCleanUpTask;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

    String response = null;

    switch (command.getFirst().toUpperCase()) {
      case "COMMAND":
        response = commandBuilder.buildList(Collections.emptyList());
        break;

      case "PING":
        response = commandBuilder.buildString("PONG");
        break;

      case "ECHO":
        response = commandBuilder.buildString(command.get(1));
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
          List<String> requestedConf = new ArrayList<>();
          String requestedConfParam = command.get(2).toLowerCase();
          requestedConf.add(requestedConfParam);

          if (requestedConfParam.equals("dir")) {
            requestedConf.add(config.getRdbDir());
          }

          if (requestedConfParam.equals("dbfilename")) {
            requestedConf.add(config.getRdbFileName());
          }

          response = commandBuilder.buildList(requestedConf);
        }
        break;

      case "INFO":
        Map<String, String> info = new HashMap<>();
        info.put("role", (config.getMasterHost() == null) ? "master" : "slave");
        info.put("master_replid", config.getReplicaId());
        info.put("master_repl_offset", "0");

        response = commandBuilder.buildMap(info);
        break;

      case "REPLCONF":
        response = commandBuilder.buildString("OK");
        break;

      case "PSYNC":
        response = commandBuilder.buildString("FULLRESYNC " + config.getReplicaId() + " 0");
        break;

      default:
        System.out.println(
            String.format("Commang: '%s' is not implemented.", command.getFirst().toUpperCase()));
    }

    if (response != null) {
      send(response);
    }
  }

  public void send(String message) throws IOException {
    outSocket.write(message.getBytes());
  }
}
