package akkaTest

import akka.actor.{Actor, ActorSystem, Props, ActorLogging, ActorRef, PoisonPill}
object AkkaExample12 extends App {
    class FirstActor extends Actor with ActorLogging {
        var child: ActorRef = context.actorOf(Props[MyActor], name = "myActor")

        override def receive: Receive = {
            // send PoisonPill to stop child
            case "stop" => child ! PoisonPill
            case x =>{
                child ! x
                log.info("received "+x)
            }
        }
        override def postStop(): Unit = {
            log.info("postStop In FirstActor")
        }
    }
    class MyActor extends Actor with ActorLogging{
        def receive = {
            case "test" => log.info("received test");
            case _      => log.info("received unknown message");
        }
        override def postStop(): Unit = {
            log.info("postStop In MyActor")
        }
    }

    val system = ActorSystem("MyActorSystem")
    val systemLog=system.log

    val firstActor = system.actorOf(Props[FirstActor], name = "firstActor")

    systemLog.info("ready to send message to firstActor")
    firstActor ! "test"
    firstActor !  123
    firstActor ! "stop"

    Thread.sleep(3000)
    system.terminate()
}
