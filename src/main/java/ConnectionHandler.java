import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import command.CommandParser;
import command.CommandResponce;

public class ConnectionHandler implements Runnable {
    private Socket connectionSocket;

    public ConnectionHandler(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    connectionSocket.getInputStream()
                )
            );

            CommandParser commandParser = new CommandParser(reader);
            CommandResponce commandResponce = new CommandResponce(
                connectionSocket.getOutputStream()
            );

            while (true) {
                ArrayList<String> command = commandParser.process();
                commandResponce.handleResponce(command);
            }
        }
        catch (IOException ioException) {
            System.out.println("Error handling connection: " + ioException);
        }
    }
}
