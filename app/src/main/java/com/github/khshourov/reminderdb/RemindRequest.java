package com.github.khshourov.reminderdb;

public class RemindRequest {
  private User user;
  private Token token;

  public RemindRequest(User user) {
    this.user = user;
  }

  public User getUser() {
    return this.user;
  }

  public Token getToken() {
    return this.token;
  }

  public void setToken(Token token) {
    this.token = token;
  }
}
