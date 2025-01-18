package conf;

public class Config {
  private String rdbDir;
  private String rdbFileName;
  private Integer port = 6379;

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
}
