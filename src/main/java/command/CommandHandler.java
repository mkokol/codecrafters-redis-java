package command;

import conf.Config;
import core.Connection;
import data.Storage;
import data.StorageCleanUpTask;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class CommandHandler {
  private final Connection connection;

  public CommandHandler(Connection connection) {
    super();

    this.connection = connection;
  }

  public void handleResponce(List<String> command) throws IOException {
    Config config = Config.getInstance();

    switch (command.getFirst().toUpperCase()) {
      case "COMMAND":
        connection.send(Collections.emptyList());
        break;

      case "PING":
        connection.send("PONG");
        break;

      case "ECHO":
        connection.send(command.get(1));
        break;

      case "SET":
        Storage.set(command.get(1), command.get(2));

        if (command.size() >= 5 && command.get(3).toUpperCase().equals("PX")) {
          (new Timer())
              .schedule(new StorageCleanUpTask(command.get(1)), Integer.parseInt(command.get(4)));
        }

        connection.sendToReplicas(command);
        connection.send("OK");
        break;

      case "GET":
        connection.send(Storage.get(command.get(1)));
        break;

      case "KEYS":
        connection.send(Storage.getKeys());
        break;

      case "CONFIG":
        if (command.get(1).toUpperCase().equals("GET")) {
          String confParam = command.get(2).toLowerCase();

          if (confParam.equals("dir")) {
            connection.send(new String[] {confParam, config.getRdbDir()});
          }

          if (confParam.equals("dbfilename")) {
            connection.send(new String[] {confParam, config.getRdbFileName()});
          }
        }
        break;

      case "INFO":
        Map<String, String> info = new HashMap<>();
        info.put("role", (config.getMasterHost() == null) ? "master" : "slave");
        info.put("master_replid", config.getReplicaId());
        info.put("master_repl_offset", "0");

        connection.send(info);
        break;

      case "REPLCONF":
        if (command.get(1).toUpperCase().equals("GETACK")) {
          connection.sendReplConf();
        } else {
          connection.send("OK");
        }
        break;

      case "PSYNC":
        connection.send("FULLRESYNC " + config.getReplicaId() + " 0");
        connection.sendEmpyDbDump();
        connection.markSocketAsReplica();
        break;

      default:
        System.out.println(
            String.format("Commang: '%s' is not implemented.", command.getFirst().toUpperCase()));
    }

    connection.increaseOffset(command);
  }
}
