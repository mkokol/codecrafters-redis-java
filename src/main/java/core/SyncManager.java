package core;

import command.CommandHandler;
import conf.Config;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SyncManager {
  private static Socket socket;

  public static void initClientSync(ReplicaHandler replicaHandler) throws IOException {
    Config config = Config.getInstance();

    if (config.getMasterHost() == null || config.getMasterPort() == null) {
      return;
    }

    socket = new Socket(config.getMasterHost(), config.getMasterPort());

    List<String[]> handshakeCommands = new ArrayList<>();
    handshakeCommands.add(new String[] {"PING"});
    handshakeCommands.add(new String[] {"REPLCONF", "listening-port", config.getPort().toString()});
    handshakeCommands.add(new String[] {"REPLCONF", "capa", "psync2"});
    handshakeCommands.add(new String[] {"PSYNC", "?", "-1"});

    Connection connection = new Connection(socket, replicaHandler);

    for (String[] command : handshakeCommands) {
      connection.send(command);
      connection.readLine();
    }
    // Reade back up file
    connection.readLine();
    connection.readLine();

    connection.makrSocketAsMaster();
    CommandHandler commandResponce = new CommandHandler(connection);

    Thread t =
        new Thread(
            () -> {
              while (true) {
                try {
                  List<String> command = connection.readCommand();
                  commandResponce.handleResponce(command);
                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
            });
    t.start();
  }

  public static void closeClientSync(Config config) throws IOException {
    if (config.getMasterHost() == null || config.getMasterPort() == null) {
      return;
    }

    socket.close();
  }
}
