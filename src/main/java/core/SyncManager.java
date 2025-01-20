package core;

import command.CommandBuilder;
import conf.Config;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class SyncManager {
  public static void clientSync(Config config) throws UnknownHostException, IOException {
    if (config.getMasterHost() == null || config.getMasterPort() == null) {
      return;
    }

    Socket socket = new Socket(config.getMasterHost(), config.getMasterPort());
    OutputStream output = socket.getOutputStream();

    CommandBuilder commandBuilder = new CommandBuilder();
    List<String> commandParam = new ArrayList<>();
    commandParam.add("PING");
    String request = commandBuilder.buildList(commandParam);

    output.write(request.getBytes());
    socket.close();
  }
}
