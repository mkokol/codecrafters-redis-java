import command.CommandParser;
import command.CommandResponce;
import conf.Config;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionHandler implements Runnable {
  private Socket connectionSocket;
  private Config config;

  public ConnectionHandler(Socket connectionSocket, Config config) {
    this.connectionSocket = connectionSocket;
    this.config = config;
  }

  public void run() {
    try {
      InputStreamReader inputStreamReader =
          new InputStreamReader(connectionSocket.getInputStream());
      BufferedReader reader = new BufferedReader(inputStreamReader);
      CommandParser commandParser = new CommandParser(reader);
      CommandResponce commandResponce =
          new CommandResponce(connectionSocket.getOutputStream(), config);

      while (true) {
        ArrayList<String> command = commandParser.process();
        commandResponce.handleResponce(command);
      }
    } catch (IOException ioException) {
      System.out.println("Error handling connection: " + ioException);
    }
  }
}
