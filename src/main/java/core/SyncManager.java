package core;

import command.CommandBuilder;
import conf.Config;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SyncManager {
  public static Socket socket;

  public static void initClientSync(Config config) throws IOException {
    if (config.getMasterHost() == null || config.getMasterPort() == null) {
      return;
    }

    socket = new Socket(config.getMasterHost(), config.getMasterPort());
    PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

    CommandBuilder commandBuilder = new CommandBuilder();
    List<String[]> handshakeCommands = new ArrayList<>();
    handshakeCommands.add(new String[] {"PING"});
    handshakeCommands.add(new String[] {"REPLCONF", "listening-port", config.getPort().toString()});
    handshakeCommands.add(new String[] {"REPLCONF", "capa", "psync2"});
    handshakeCommands.add(new String[] {"PSYNC", "?", "-1"});

    for (String[] command : handshakeCommands) {
      output.print(commandBuilder.buildList(Arrays.asList(command)));
      output.flush();
      socket.getInputStream().read();
    }
  }

  public static void closeClientSync(Config config) throws IOException {
    if (config.getMasterHost() == null || config.getMasterPort() == null) {
      return;
    }

    socket.close();
  }
}
