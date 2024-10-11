package command;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import data.Storage;


public class CommandResponce {
    private OutputStream outSocket;

    public CommandResponce(OutputStream outSocket) {
        this.outSocket = outSocket;
    }

    public void handleResponce(ArrayList<String> command) throws IOException {
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
                    send("+OK\r\n");
                break;
            case "GET":
                    String val = Storage.get(command.get(1));
                    send("$" + val.length() + "\r\n" + val + "\r\n");
                break;
        }
    }

    public void send(String message) throws IOException {
        outSocket.write(
            message.getBytes()
        );
    }
}
