package core;

import command.CommandBuilder;
import command.CommandParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Connection {
  private final BufferedReader reader;
  private final OutputStream writer;
  private final CommandParser commandParser;
  private final CommandBuilder commandBuilder;
  private final ReplicaHandler replicaHandler;
  private Integer offset;

  public Connection(Socket connectionSocket, ReplicaHandler replicaHandler) throws IOException {
    super();

    reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
    writer = connectionSocket.getOutputStream();

    commandParser = new CommandParser(reader);
    commandBuilder = new CommandBuilder();

    this.replicaHandler = replicaHandler;
    offset = 0;
  }

  public List<String> readCommand() throws IOException {
    List<String> cmd = Collections.emptyList();

    while (cmd.size() == 0) {
      cmd = commandParser.process();
    }

    return cmd;
  }

  public void send(String data) throws IOException {
    sendMessage(commandBuilder.build(data));
  }

  public void send(List<String> data) throws IOException {
    sendMessage(commandBuilder.build(data));
  }

  public void send(String[] data) throws IOException {
    send(Arrays.asList(data));
  }

  public void send(Map<String, String> data) throws IOException {
    sendMessage(commandBuilder.build(data));
  }

  public void sendMessage(String message) throws IOException {
    if (isMasterSocket()) {
      return;
    }

    sendData(message);
  }

  public void sendReplConf() throws IOException {
    List<String> data = Arrays.asList(new String[] {"REPLCONF", "ACK", offset.toString()});
    sendData(commandBuilder.build(data));
  }

  public void sendEmpyDbDump() throws IOException {
    String emptyBackUpFile =
        "UkVESVMwMDEx+glyZWRpcy12ZXIFNy4yLjD6CnJlZGlzLWJpdHPAQPoFY3RpbWXCbQi8ZfoIdXNlZC1tZW3CsMQQAPoIYW9mLWJhc2XAAP/wbjv+wP9aog==";
    byte[] decoded = Base64.getDecoder().decode(emptyBackUpFile);
    writer.write(("$" + decoded.length + "\r\n").getBytes());
    writer.write(decoded);
    writer.flush();
  }

  public void sendData(String message) throws IOException {
    writer.write(message.getBytes());
    writer.flush();
  }

  public void readLine() throws IOException {
    reader.readLine();
  }

  public void markSocketAsReplica() {
    replicaHandler.addSocket(writer);
  }

  public void makrSocketAsMaster() {
    replicaHandler.setMasterSocket(writer);
  }

  public boolean isMasterSocket() {
    return replicaHandler.getMasterSocket() == writer;
  }

  public void sendToReplicas(List<String> command) {
    replicaHandler.sendToReplicas(writer, commandBuilder.build(command));
  }

  public void increaseOffset(List<String> command) {
    offset += commandBuilder.build(command).length();
  }
}
