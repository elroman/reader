package controllers;

import actors.proto.AccountMessageReq;
import actors.proto.GetResultMessageReq;
import actors.proto.GetResultMessageRes;
import actors.proto.TotalListRes;
import akka.actor.ActorRef;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.entities.Account;
import play.libs.Akka;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.Future;
import views.html.reader.index;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static akka.pattern.Patterns.ask;

public class ReaderController extends Controller {

    @Inject
    @Named("eventProcessorSupervisor")
    ActorRef eventProcessorSupervisor;

    public Result index() {

        return ok(index.render("page load"));
    }


    public Result load() {
        Queue<Account> accounts = new LinkedList<>();
        InputStream resource = getClass().getResourceAsStream("/amountList.json");
        ObjectMapper mapper = new ObjectMapper();

        try {
            accounts = mapper.readValue(resource, new TypeReference<LinkedList<Account>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        Future<Object> res = navigate(accounts, eventProcessorSupervisor);

        Future<Map<String, Integer>> result = res.flatMap(new Mapper<Object, Future<Map<String, Integer>>>() {
            public Future<Map<String, Integer>> apply(Object nodes) {
                Map<String, Integer> mapResult = new HashMap<>();
                Future<Object> totalList = ask(eventProcessorSupervisor, new GetResultMessageReq(), 10000);


                Future<Boolean> complete = totalList.flatMap(new Mapper<Object, Future<Boolean>>() {
                    @Override
                    public Future<Boolean> apply(Object parameter) {
                        TotalListRes totalListObj = (TotalListRes) parameter;


                        totalListObj.tolalList.forEach((account) -> {

                            GetResultMessageRes res = (GetResultMessageRes) account;

                            System.out.println("actor id: " + res.accountId + "  sum = " + res.sum);
//                           ОСТАЕТСЯ РЕЗУЛЬТАТ ВЫВЕСТИ В ФАЙЛ!!!
                        });

//                        ТУТ У МЕНЯ СЕЛА БАТАРЕЯ , А ЗАРЯДКА НА РАБОТЕ :(((((   !!!!!!!!!!!!

                        return Futures.successful(true);
                    }
                }, Akka.system().dispatcher());

                return Futures.successful(mapResult);
            }
        }, Akka.system().dispatcher());

        return ok(index.render("start load"));
    }


    Future<Object> navigate(final Queue<Account> accounts, final ActorRef actorRef) {
        Account account = accounts.poll();

        if (account != null) {
            AccountMessageReq mes = new AccountMessageReq(account);

            Future<Object> r = ask(actorRef, mes, 10000);

            return r.flatMap(new Mapper<Object, Future<Object>>() {
                @Override
                public Future<Object> apply(Object parameter) {
                    return navigate(accounts, actorRef);
                }
            }, Akka.system().dispatcher());
        }

        return Futures.successful(null);
    }


}
