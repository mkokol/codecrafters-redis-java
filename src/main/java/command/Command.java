package command;

import java.util.ArrayList;

public class Command {
    private String name;
    private ArrayList<String> params;

    public Command(
        String name,
        ArrayList<String> params
    ) {
        this.name = name;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getParams() {
        return params;
    }
}
