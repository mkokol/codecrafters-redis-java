package command;

import conf.Config;
import core.ReplicaHandler;
import data.Storage;
import data.StorageCleanUpTask;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class CommandHandler {
  private final OutputStream outSocket;
  private final Config config;
  private final CommandBuilder commandBuilder;
  private final ReplicaHandler replicaHandler;

  public CommandHandler(
      OutputStream outSocket,
      Config config,
      CommandBuilder commandBuilder,
      ReplicaHandler replicaHandler) {
    this.outSocket = outSocket;
    this.config = config;
    this.commandBuilder = commandBuilder;
    this.replicaHandler = replicaHandler;
  }

  public void handleResponce(List<String> command) throws IOException {
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

        replicaHandler.sendToReplicas(outSocket, commandBuilder.buildList(command));
        response = commandBuilder.buildString("OK");

        break;

      case "GET":
        String val = Storage.get(command.get(1));
        response = (val != null) ? commandBuilder.buildString(val) : "$-1\r\n";
        break;

      case "KEYS":
        response = commandBuilder.buildList(Storage.getKeys());
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
        if (command.get(1).toUpperCase().equals("GETACK")) {
          response = commandBuilder.buildList(Arrays.asList(new String[] {"REPLCONF", "ACK", "0"}));
        } else {
          response = commandBuilder.buildString("OK");
        }
        break;

      case "PSYNC":
        send(commandBuilder.buildString("FULLRESYNC " + config.getReplicaId() + " 0"));
        outSocket.flush();
        sendEmpyDbDump();

        replicaHandler.addSocket(outSocket);
        break;

      default:
        System.out.println(
            String.format("Commang: '%s' is not implemented.", command.getFirst().toUpperCase()));
    }

    if (outSocket == replicaHandler.getMasterSocket()
        && !command.getFirst().toUpperCase().equals("REPLCONF")) {
      return;
    }

    if (response != null) {
      send(response);
    }
  }

  public void send(String message) throws IOException {
    outSocket.write(message.getBytes());
    outSocket.flush();
  }

  public void sendEmpyDbDump() throws IOException {
    String emptyBackUpFile =
        "UkVESVMwMDEx+glyZWRpcy12ZXIFNy4yLjD6CnJlZGlzLWJpdHPAQPoFY3RpbWXCbQi8ZfoIdXNlZC1tZW3CsMQQAPoIYW9mLWJhc2XAAP/wbjv+wP9aog==";
    byte[] decoded = Base64.getDecoder().decode(emptyBackUpFile);
    outSocket.write(("$" + decoded.length + "\r\n").getBytes());
    outSocket.write(decoded);
    outSocket.flush();
  }
}
