package data;

import conf.Config;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class RdbFile {
  static final byte resizeDb = (byte) 0xFB;
  static final byte msExpiry = (byte) 0xFC;

  public static void parse(Config conf) throws IOException {
    Path rdbFilePath = Paths.get(conf.getRdbDir() + '/' + conf.getRdbFileName());

    if (!Files.exists(rdbFilePath)) {
      return;
    }

    byte[] rdbFileContent = Files.readAllBytes(rdbFilePath);

    // InputStream fis = new FileInputStream(new File(file));

    int dbStart = 0;
    byte ch = rdbFileContent[dbStart];

    while (ch != resizeDb) {
      dbStart++;
      ch = rdbFileContent[dbStart];
    }

    int size = (int) rdbFileContent[dbStart + 1];
    int keyStart = dbStart + 4;

    for (int i = 0; i < size; i++) {
      Long ttl = null;

      if (rdbFileContent[keyStart - 1] == msExpiry) {
        ByteBuffer ttlData =
            ByteBuffer.wrap(Arrays.copyOfRange(rdbFileContent, keyStart, keyStart + 8));
        ttlData.order(ByteOrder.LITTLE_ENDIAN);
        ttl = ttlData.getLong();

        keyStart += 9;
      }

      int keyLen = (int) rdbFileContent[keyStart];
      String key =
          new String(Arrays.copyOfRange(rdbFileContent, keyStart + 1, keyStart + keyLen + 1));

      int valStart = keyStart + keyLen + 1;
      int valLen = (int) rdbFileContent[valStart];
      String val =
          new String(Arrays.copyOfRange(rdbFileContent, valStart + 1, valStart + valLen + 1));

      Storage.set(key, val, ttl);

      keyStart = valStart + valLen + 2;
    }
  }
}
