package command;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class CommandParser {
    private BufferedReader reader;

    public CommandParser(BufferedReader reader) {
        this.reader = reader;
    }

    public Command pars() throws IOException {
        String line = reader.readLine();
        int size = Integer.valueOf(line.substring(1));
        System.out.println("> " + size);

        reader.readLine();
        String commandName = reader.readLine().toUpperCase();
        System.out.println("CN > " + commandName);

        ArrayList<String> commandParams = new ArrayList<String>();

        for (int i = 1; i < size; i++) {
            reader.readLine();
            commandParams.add(reader.readLine());
            System.out.println("CP > " + commandParams.getLast());
        }

        return new Command(
            commandName,
            commandParams
        );
    }
}
