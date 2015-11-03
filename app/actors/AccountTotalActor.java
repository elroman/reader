package actors;

import actors.proto.AccountMessageReq;
import actors.proto.GetResultMessageReq;
import actors.proto.GetResultMessageRes;
import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.dispatch.Futures;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

import static akka.pattern.Patterns.pipe;

public class AccountTotalActor extends AbstractActor {
    final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);
    int sum = 0;
    String accountId;

    @Override
    public void preStart() throws Exception {
        super.preStart();
    }

    public static Props props(final String accountId) {
        return Props.create(AccountTotalActor.class, () -> new AccountTotalActor(accountId));
    }

    public AccountTotalActor(String accountId) {

        this.accountId = accountId;

        receive(ReceiveBuilder
                        .match(AccountMessageReq.class, this::addAmount)
                        .match(GetResultMessageReq.class, this::getResult)
                        .build()
        );
    }

    void addAmount(AccountMessageReq evt) {
        sum = sum + evt.account.amount;
//        System.out.println("actor id: " + accountId + "   sum = " + sum);

        pipe(Futures.successful(true), getContext().dispatcher()).to(sender());
    }

    void getResult(GetResultMessageReq evt) {

        pipe(Futures.successful(new GetResultMessageRes(accountId, sum)), getContext().dispatcher()).to(sender());

    }

}
