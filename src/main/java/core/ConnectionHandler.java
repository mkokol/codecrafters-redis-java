package core;

import command.CommandHandler;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ConnectionHandler implements Runnable {
  private final Socket socket;
  private final ReplicaHandler replicaHandler;

  public ConnectionHandler(Socket socket, ReplicaHandler replicaHandler) {
    this.socket = socket;
    this.replicaHandler = replicaHandler;
  }

  public void run() {
    try {
      Connection connection = new Connection(socket, replicaHandler);
      CommandHandler commandResponce = new CommandHandler(connection);

      while (true) {
        List<String> command = connection.readCommand();
        commandResponce.handleResponce(command);
      }
    } catch (IOException ioException) {
      System.out.println("Error handling connection: " + ioException);
    }
  }
}
