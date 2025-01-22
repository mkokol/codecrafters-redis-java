package core;

import command.CommandBuilder;
import command.CommandHandler;
import command.CommandParser;
import conf.Config;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

public class ConnectionHandler implements Runnable {
  private final Socket connectionSocket;
  private final Config config;
  private final ReplicaHandler replicaHandler;

  public ConnectionHandler(Socket connectionSocket, Config config, ReplicaHandler replicaHandler) {
    this.connectionSocket = connectionSocket;
    this.config = config;
    this.replicaHandler = replicaHandler;
  }

  public void run() {
    try {
      InputStreamReader inputStreamReader =
          new InputStreamReader(connectionSocket.getInputStream());
      BufferedReader reader = new BufferedReader(inputStreamReader);
      CommandParser commandParser = new CommandParser(reader);
      CommandBuilder commandBuilder = new CommandBuilder();
      CommandHandler commandResponce =
          new CommandHandler(
              connectionSocket.getOutputStream(), config, commandBuilder, replicaHandler);

      while (true) {
        List<String> command = commandParser.process();
        commandResponce.handleResponce(command);
      }
    } catch (IOException ioException) {
      System.out.println("Error handling connection: " + ioException);
    }
  }
}
