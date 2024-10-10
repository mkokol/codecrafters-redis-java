import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args){
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        int port = 6379;

        try {
            serverSocket = new ServerSocket(port);
            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors
            serverSocket.setReuseAddress(true);

            System.out.println("Listen on: localhost:" + port);

            // Wait for connection from client.
            clientSocket = serverSocket.accept();

            System.out.println("Read streem");
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    clientSocket.getInputStream()
                )
            );

            String line = "";

            while (true) {
                try {
                    line = reader.readLine();

                    if (line.equals("DOCS")) {
                        clientSocket.getOutputStream().write(
                            "*0\r\n".getBytes()
                        );
                    }

                    if (line.equals("PING")) {
                        clientSocket.getOutputStream().write(
                            "+PONG\r\n".getBytes()
                        );
                    }
                }
                catch (IOException i) {
                    System.out.println(i);
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }
}

