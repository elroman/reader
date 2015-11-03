import actors.EventProcessorSupervisorActor;
import com.google.inject.AbstractModule;
import play.libs.akka.AkkaGuiceSupport;

public class MyModule extends AbstractModule implements AkkaGuiceSupport {

    @Override
    protected void configure() {

        bindActor(EventProcessorSupervisorActor.class, "eventProcessorSupervisor");

    }
}