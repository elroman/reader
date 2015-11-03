package models.entities;

public class Account {

  public String accountId;
  public int amount;

  public Account(String accountId, int amount) {
    this.accountId = accountId;
    this.amount = amount;
  }

  public Account() {
  }


}
