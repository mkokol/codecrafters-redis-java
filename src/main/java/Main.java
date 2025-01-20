import conf.Config;
import core.ConnectionHandler;
import core.SyncManager;
import data.RdbFile;
import data.Storage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class Main {
  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    Config config = new Config();
    Storage.runCleanUp(10, TimeUnit.MINUTES);

    for (int i = 0; i < args.length; i += 2) {
      switch (args[i]) {
        case "--dir":
          config.setRdbDir(args[i + 1]);
          break;

        case "--dbfilename":
          config.setRdbFileName(args[i + 1]);
          break;

        case "--port":
          config.setPort(Integer.parseInt(args[i + 1]));
          break;

        case "--replicaof":
          String[] masterHostAndPort = args[i + 1].split(" ");
          config.setMasterHost(masterHostAndPort[0]);
          config.setMasterPort(masterHostAndPort[1]);
          break;

        default:
          break;
      }
    }

    RdbFile.parse(config);
    SyncManager.clientSync(config);

    try {
      serverSocket = new ServerSocket(config.getPort());
      // Since the tester restarts your program quite often, setting SO_REUSEADDR
      // ensures that we don't run into 'Address already in use' errors
      serverSocket.setReuseAddress(true);
      System.out.println("Listen on: localhost:" + config.getPort());

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
