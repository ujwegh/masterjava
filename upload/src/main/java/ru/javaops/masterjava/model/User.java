package ru.javaops.masterjava.model;

public class User {
  private Integer id;
  private String name;
  private String email;
  private String flag;

  public User(String name, String email, String flag) {
    this(null, name, email, flag);
  }

  public User(Integer id, String name, String email, String flag) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.flag = flag;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFlag() {
    return flag;
  }

  public void setFlag(String flag) {
    this.flag = flag;
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", email='" + email + '\'' +
        ", flag='" + flag + '\'' +
        '}';
  }
}
