package com.github.khshourov.reminderdb;

public class RemindRequest {
  private User user;

  public RemindRequest(User user) {
    this.user = user;
  }

  public User getUser() {
    return this.user;
  }
}
