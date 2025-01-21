package core;

import command.CommandBuilder;
import conf.Config;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SyncManager {
  private static Socket socket;

  public static void initClientSync(Config config, ReplicaHandler replicaHandler)
      throws IOException {
    if (config.getMasterHost() == null || config.getMasterPort() == null) {
      return;
    }

    socket = new Socket(config.getMasterHost(), config.getMasterPort());
    PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    CommandBuilder commandBuilder = new CommandBuilder();
    List<String[]> handshakeCommands = new ArrayList<>();
    handshakeCommands.add(new String[] {"PING"});
    handshakeCommands.add(new String[] {"REPLCONF", "listening-port", config.getPort().toString()});
    handshakeCommands.add(new String[] {"REPLCONF", "capa", "psync2"});
    handshakeCommands.add(new String[] {"PSYNC", "?", "-1"});

    for (String[] command : handshakeCommands) {
      output.print(commandBuilder.buildList(Arrays.asList(command)));
      output.flush();
      in.readLine();
    }

    // Reade back up file
    in.readLine();

    replicaHandler.addSocket(socket.getOutputStream());

    ConnectionHandler connectionHandler = new ConnectionHandler(socket, config, replicaHandler);
    new Thread(connectionHandler).start();
  }

  public static void closeClientSync(Config config) throws IOException {
    if (config.getMasterHost() == null || config.getMasterPort() == null) {
      return;
    }

    socket.close();
  }
}
