package data;

import conf.Config;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class RdbFile {
  final byte resizeDb = (byte) 0xFB;

  public RdbFile(Config conf) throws IOException {
    super();

    Path rdbFilePath = Paths.get(conf.getRdbDir() + '/' + conf.getRdbFileName());

    if (!Files.exists(rdbFilePath)) {
      return;
    }

    byte[] rdbFileContent = Files.readAllBytes(rdbFilePath);
    int dbStart = 0;
    byte ch = rdbFileContent[dbStart];

    while (ch != resizeDb) {
      dbStart++;
      ch = rdbFileContent[dbStart];
    }

    int size = (int) rdbFileContent[dbStart + 1];
    int keyStart = dbStart + 4;

    for (int i = 0; i < size; i++) {
      int keyLen = (int) rdbFileContent[keyStart];
      String key =
          new String(Arrays.copyOfRange(rdbFileContent, keyStart + 1, keyStart + keyLen + 1));

      int valStart = keyStart + keyLen + 1;
      int valLen = (int) rdbFileContent[valStart];
      String val =
          new String(Arrays.copyOfRange(rdbFileContent, valStart + 1, valStart + valLen + 1));

      keyStart = valStart + valLen + 1;

      Storage.set(key, val);
    }
  }
}
