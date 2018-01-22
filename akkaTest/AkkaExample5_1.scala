package akkaTest

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

object AkkaExample5_1 extends App {

    class FirstActor extends Actor with ActorLogging {
        var child: ActorRef = _

        override def preStart(): Unit = {
            log.info("preStart() in FirstActor")
            child = context.actorOf(Props[MyActor], name="myActor")
        }

        override def receive: Receive = {
            case x => child ! x; log.info("received " + x)
        }
    }

    class MyActor extends Actor with ActorLogging {
        self ! "message from self reference"

        override def receive: Receive = {
            case "test" => log.info("received test"); sender() ! "message from MyActor"
            case "message from self reference" => log.info("log message from self reference")
            case _ => log.info("received unknown message")
        }
    }

    val system = ActorSystem("MyActorSystem")
    val systemLog = system.log

    val firstActor = system.actorOf(Props[FirstActor], name="firstActor")
    Thread.sleep(3000)

    systemLog.info("ready to send message to firstActor")
    firstActor ! "test"
    firstActor ! 123

    Thread.sleep(3000)
    system.terminate()

}
