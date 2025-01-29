package core;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ReplicaHandler {
  private List<OutputStream> replicasScoket;
  private OutputStream masterScoket;

  public ReplicaHandler() {
    super();

    replicasScoket = new ArrayList<>();
  }

  public void addSocket(OutputStream replicaSocket) {
    replicasScoket.add(replicaSocket);
  }

  public void setMasterSocket(OutputStream outputStream) {
    masterScoket = outputStream;

    addSocket(outputStream);
  }

  public OutputStream getMasterSocket() {
    return masterScoket;
  }

  public void sendToReplicas(OutputStream current, String message) {
    for (OutputStream outSocket : replicasScoket) {
      if (outSocket == current) {
        continue;
      }

      try {
        outSocket.write(message.getBytes());
        outSocket.flush();
      } catch (IOException e) {
        // e.printStackTrace();
        System.out.println("Errors for sending replication info");
      }
    }
  }

  public Integer getSize() {
    return replicasScoket.size();
  }
}
