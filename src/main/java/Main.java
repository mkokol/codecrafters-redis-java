import conf.Config;
import core.ConnectionHandler;
import core.ReplicaHandler;
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
    Config config = Config.getInstance();
    ReplicaHandler replicaHandler = new ReplicaHandler();
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

    System.out.println("Listen on: localhost:" + config.getPort());

    try {
      RdbFile.parse();
      SyncManager.initClientSync(replicaHandler);

      serverSocket = new ServerSocket(config.getPort());
      // Since the tester restarts your program quite often, setting SO_REUSEADDR
      // ensures that we don't run into 'Address already in use' errors
      serverSocket.setReuseAddress(true);

      while (true) {
        ConnectionHandler connectionHandler =
            new ConnectionHandler(serverSocket.accept(), replicaHandler);
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

    SyncManager.closeClientSync(config);
  }
}
