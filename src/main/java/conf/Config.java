package conf;

import java.security.SecureRandom;

public class Config {
  private static Config config;

  private String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  private String rdbDir;
  private String rdbFileName;
  private Integer port = 6379;
  private String masterHost;
  private Integer masterPort;
  private String replicaId;

  private Config() {
    super();

    replicaId = randomString(40);
  }

  public static Config getInstance() {
    if (config == null) {
      config = new Config();
    }

    return config;
  }

  public String getRdbDir() {
    return rdbDir;
  }

  public void setRdbDir(String rdbDir) {
    this.rdbDir = rdbDir;
  }

  public String getRdbFileName() {
    return rdbFileName;
  }

  public void setRdbFileName(String rdbFileName) {
    this.rdbFileName = rdbFileName;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public String getMasterHost() {
    return masterHost;
  }

  public void setMasterHost(String masterHost) {
    this.masterHost = masterHost;
  }

  public Integer getMasterPort() {
    return masterPort;
  }

  public void setMasterPort(Integer masterPort) {
    this.masterPort = masterPort;
  }

  public void setMasterPort(String masterPort) {
    this.masterPort = Integer.valueOf(masterPort);
  }

  public String getReplicaId() {
    return replicaId;
  }

  private String randomString(int len) {
    SecureRandom secureRandom = new SecureRandom();
    StringBuilder responce = new StringBuilder(len);

    for (int i = 0; i < len; i++) {
      responce.append(characters.charAt(secureRandom.nextInt(characters.length())));
    }

    return responce.toString();
  }
}
