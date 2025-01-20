package conf;

public class Config {
  private String rdbDir;
  private String rdbFileName;
  private Integer port = 6379;
  private String masterHost;
  private Integer masterPort;

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
}
