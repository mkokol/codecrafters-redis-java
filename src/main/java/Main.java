import conf.Config;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    int port = 6379;
    Config config = new Config();

    for (int i = 0; i < args.length; i += 2) {
      switch (args[i]) {
        case "--dir":
          config.setRdbDir(args[i + 1]);
          break;
        case "--dbfilename":
          config.setRdbFileName(args[i + 1]);
          break;

        default:
          break;
      }
    }

    System.out.println("RdbDir:" + config.getRdbDir());
    System.out.println("RdbFileName:" + config.getRdbFileName());

    try {
      serverSocket = new ServerSocket(port);
      // Since the tester restarts your program quite often, setting SO_REUSEADDR
      // ensures that we don't run into 'Address already in use' errors
      serverSocket.setReuseAddress(true);
      System.out.println("Listen on: localhost:" + port);

      while (true) {
        ConnectionHandler connectionHandler = new ConnectionHandler(serverSocket.accept(), config);
        new Thread(connectionHandler).start();
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    } finally {
      try {
        if (clientSocket != null) {
          clientSocket.close();
        }
      } catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
      }
    }
  }
}
