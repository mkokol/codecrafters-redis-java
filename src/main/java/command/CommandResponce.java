package command;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Timer;

import data.Storage;
import data.StorageCleanUpTask;


public class CommandResponce {
    private OutputStream outSocket;

    public CommandResponce(OutputStream outSocket) {
        this.outSocket = outSocket;
    }

    public void handleResponce(ArrayList<String> command) throws IOException {
        if (command.size() == 0) {
            return;
        }

        switch (command.getFirst().toUpperCase()) {
            case "COMMAND":
                    send("*0\r\n");
                break;
            case "PING":
                    send("+PONG\r\n");
                break;
            case "ECHO":
                    send("+" + command.get(1) + "\r\n");
                break;
            case "SET":
                    Storage.set(command.get(1), command.get(2));

                    if (
                        command.size() >= 5
                        && command.get(3).toUpperCase().equals("PX")
                    ) {
                        (new Timer()).schedule(
                            new StorageCleanUpTask(command.get(1)),
                            Integer.parseInt(command.get(4))
                        );
                    }

                    send("+OK\r\n");
                break;
            case "GET":
                    String val = Storage.get(command.get(1));

                    if (val != null) {
                        send("$" + val.length() + "\r\n" + val + "\r\n");
                    } else {
                        send("$-1\r\n");
                    }
                break;
        }
    }

    public void send(String message) throws IOException {
        outSocket.write(
            message.getBytes()
        );
    }
}
