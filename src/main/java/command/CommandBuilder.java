package command;

import java.util.List;

public class CommandBuilder {
  public String buildString(String data) {
    return String.format("+%s\r\n", data);
  }

  public String buildList(List<String> data) {
    StringBuilder responce = new StringBuilder("*" + String.valueOf(data.size()) + "\r\n");

    for (String record : data) {
      responce.append("$" + String.valueOf(record.length()) + "\r\n" + record + "\r\n");
    }

    return responce.toString();
  }
}
