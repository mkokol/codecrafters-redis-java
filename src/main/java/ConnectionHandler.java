import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

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

            String line = "";

            while (true) {
                line = reader.readLine();

                if (line.equals("DOCS")) {
                    connectionSocket.getOutputStream().write(
                        "*0\r\n".getBytes()
                    );
                }

                if (line.equals("PING")) {
                    connectionSocket.getOutputStream().write(
                        "+PONG\r\n".getBytes()
                    );
                }
            }
        }
        catch (IOException ioException) {
            System.out.println("Error handling connection: " + ioException);
        }
    }
}
