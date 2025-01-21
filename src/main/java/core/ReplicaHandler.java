package core;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ReplicaHandler {
  private List<OutputStream> replicasScoket;

  public ReplicaHandler() {
    super();

    replicasScoket = new ArrayList<>();
  }

  public void addSocket(OutputStream replicaSocket) {
    replicasScoket.add(replicaSocket);
  }

  public void sendToReplicas(String message) throws IOException {
    for (OutputStream outSocket : replicasScoket) {
      outSocket.write(message.getBytes());
      outSocket.flush();
    }
  }
}
