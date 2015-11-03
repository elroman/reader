package actors;

import actors.proto.AccountMessageReq;
import actors.proto.GetResultMessageReq;
import actors.proto.TotalListRes;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import scala.concurrent.Future;

import java.util.List;
import java.util.Map;

import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;

public class EventProcessorSupervisorActor extends AbstractActor {
    final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);
    final Map<String, ActorRef> processors = Maps.newHashMap();


    @Override
    public void preStart() throws Exception {
        super.preStart();
    }

    public EventProcessorSupervisorActor() {
        receive(ReceiveBuilder
                        .match(AccountMessageReq.class, this::handleEvent)
                        .match(GetResultMessageReq.class, this::getResult)
                        .build()
        );
    }

    ActorRef actorOf(String accountId) {
        ActorRef actorRef = processors.get(accountId);

        if (actorRef == null) {
            actorRef = getContext().actorOf(AccountTotalActor.props(accountId), "account:" + accountId);
            context().watch(actorRef);
            processors.put(accountId, actorRef);
        }

        return actorRef;
    }

    void handleEvent(AccountMessageReq evt) {
        ActorRef actorRef = actorOf(evt.account.accountId);
        actorRef.forward(evt, context());
    }

    void getResult(GetResultMessageReq evt) {

        List<Future<Object>> counters = Lists.newArrayList();

        processors.forEach((accountId, actorRef) -> {
            counters.add(ask(actorRef, evt, 10000));
        });

        Future<Iterable<Object>> completeList = Futures.sequence(counters, context().dispatcher());

        Future<TotalListRes> resp = completeList.flatMap(new Mapper<Iterable<Object>, Future<TotalListRes>>() {
            @Override
            public Future<TotalListRes> apply(Iterable<Object> p) {

                return Futures.successful(new TotalListRes(p));
            }
        }, context().dispatcher());

        pipe(resp, getContext().dispatcher()).to(sender());

    }
}
