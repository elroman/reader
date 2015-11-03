package actors.proto;

import models.entities.Account;

public class AccountMessageReq {
    public Account account;

    public AccountMessageReq(Account account) {
        this.account = account;
    }
}
