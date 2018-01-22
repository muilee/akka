package akkaTest

import akka.actor.ActorRef
import akka.actor.{Actor, ActorLogging}
import akka.actor.ActorSystem
import akka.actor.Props
object AkkaExample5 extends App {


    class FirstActor extends Actor with ActorLogging {
        var child: ActorRef = _
        // invoke before start actor
        override def preStart(): Unit = {
            log.info("preStart() in FirstActor")
            child = context.actorOf(Props[MyActor], name = "myChild")
        }

        override def receive: Receive = {
            case x => child ! x; log.info("received "+x)
        }
        // invoke after stop actor
        override def postStop(): Unit = {
            log.info("postStop() in FirstActor")

            // stop child actor
            //context.stop(child)
        }
    }

    class MyActor extends Actor with ActorLogging {
        override def preStart(): Unit = {
            log.info("preStart() in MyActor")
        }

        override def receive: Receive = {
            case "test" => log.info("received test")
            case _      => log.info("received unknown message")
        }

        override def postStop(): Unit = {
            log.info("postStop() in MyActor")
        }
    }

    val system = ActorSystem("MyActorSystem")
    val systemLog = system.log
    val firstActor = system.actorOf(Props[FirstActor], name="firstActor")

    Thread.sleep(3000)

    // send message
    firstActor ! "test"
    firstActor ! 123

    Thread.sleep(3000)

    // firstActor will stop child myActor before stopping itself cause firstActor is supervisor of myActor
    system.terminate()

}
