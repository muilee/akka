package akkaTest

import akka.actor.{Actor, ActorLogging}
import akka.actor.ActorSystem
import akka.actor.Props

object AkkaExample6 extends App {

    class FirstActor extends Actor with ActorLogging {
        override def receive: Receive = {
            case "test" => log.info("received test")
        }

        // 123 will be handled in this function because receive function doesn't have any case to handle
        override def unhandled(message: Any): Unit = {
            log.info("unhandled message is {}", message)
        }
    }

    val system = ActorSystem("MyActorSystem")
    val systemLog=system.log

    val firstActor = system.actorOf(Props[FirstActor], name="firstActor")

    systemLog.info("ready to send message to firstActor")
    firstActor ! "test"
    firstActor ! 123

    Thread.sleep(3000)
    system.terminate()

}
