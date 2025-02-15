package command;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandParser {
  private BufferedReader reader;

  public final byte asteriskByte = '*';
  public final byte dollarByte = '$';

  public CommandParser(BufferedReader reader) {
    this.reader = reader;
  }

  public List<String> process() throws IOException {
    byte b = (byte) reader.read();

    switch (b) {
      case dollarByte:
        return processBulkRequest();

      case asteriskByte:
        return processMultiBulkRequest();

      default:
        return new ArrayList<String>();
    }
  }

  public ArrayList<String> processMultiBulkRequest() throws IOException {
    int len = readIntCrLf();
    ArrayList<String> record = new ArrayList<String>();

    for (int i = 0; i < len; i++) {
      record.addAll(process());
    }

    return record;
  }

  public ArrayList<String> processBulkRequest() throws IOException {
    int len = readIntCrLf();

    if (len == -1) {
      return null;
    }

    char[] cbuf = new char[len];

    reader.read(cbuf);
    reader.read(); // get rid of \r
    reader.read(); // get rid of \n

    ArrayList<String> record = new ArrayList<String>();
    record.add(new String(cbuf));

    return record;
  }

  public int readIntCrLf() throws IOException {
    byte b = '0';
    int size = 0;

    while (b != '\r') {
      size = size * 10 + (b - '0');
      b = (byte) reader.read();
    }

    reader.read(); // get rid of \n

    return size;
  }
}
