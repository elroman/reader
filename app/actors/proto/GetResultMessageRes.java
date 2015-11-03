package actors.proto;

public class GetResultMessageRes {
    public String accountId;
    public int sum;

    public GetResultMessageRes(String accountId, int sum) {
        this.accountId = accountId;
        this.sum = sum;
    }
}
