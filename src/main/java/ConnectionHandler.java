import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import command.Command;
import command.CommandParser;

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

            while (true) {
                Command command = commandParser.pars();

                if (command.getName().equals("COMMAND")) {
                    connectionSocket.getOutputStream().write(
                        "*0\r\n".getBytes()
                    );
                }

                if (command.getName().equals("PING")) {
                    connectionSocket.getOutputStream().write(
                        "+PONG\r\n".getBytes()
                    );
                }

                if (command.getName().equals("ECHO")) {
                    connectionSocket.getOutputStream().write(
                        ("+" + command.getParams().getFirst() + "\r\n").getBytes()
                    );
                }
                
            }
        }
        catch (IOException ioException) {
            System.out.println("Error handling connection: " + ioException);
        }
    }
}
