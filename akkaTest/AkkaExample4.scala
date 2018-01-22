package akkaTest

import akka.actor.{Actor, ActorLogging}
import akka.actor.Props
import akka.actor.ActorSystem

object AkkaExample4 extends App {

    class FirstActor extends Actor with ActorLogging{

        val child = context.actorOf(Props[MyActor], name="myChild")

        override def receive: Receive = {
            case x => child ! x; log.info("recieved" + x)
        }
    }

    class MyActor extends Actor with ActorLogging {
        override def receive: Receive = {
            case "test" => log.info("received test")
            case _ => log.info("received unknown message")
        }
    }

    val system = ActorSystem("MyActorSystem")
    val systemLog = system.log

    val firstActor = system.actorOf(Props[FirstActor], name="firstActor")
    systemLog.info("ready to send message to firstActor")
    firstActor !"test"
    firstActor ! 123
    Thread.sleep(5000)
    system.terminate()
}
