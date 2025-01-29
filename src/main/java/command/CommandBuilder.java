package command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandBuilder {
  public String build(Integer data) {
    return String.format(":%d\r\n", data);
  }

  public String build(String data) {
    if (data == null) {
      return "$-1\r\n";
    }

    return String.format("+%s\r\n", data);
  }

  public String build(List<String> data) {
    StringBuilder responce = new StringBuilder("*" + String.valueOf(data.size()) + "\r\n");

    for (String record : data) {
      responce.append("$" + String.valueOf(record.length()) + "\r\n" + record + "\r\n");
    }

    return responce.toString();
  }

  public String build(Map<String, String> data) {
    List<String> infoResponce = new ArrayList<>();

    for (var entry : data.entrySet()) {
      infoResponce.add(entry.getKey() + ":" + entry.getValue());
    }

    String infoResponceMsg = String.join("\r\n", infoResponce);

    return "$" + String.valueOf(infoResponceMsg.length()) + "\r\n" + infoResponceMsg + "\r\n";
  }
}
