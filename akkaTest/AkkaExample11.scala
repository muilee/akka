package akkaTest

import akka.actor.{Actor, ActorSystem, Props, ActorLogging, ActorRef}
object AkkaExample11 extends App {
    class FirstActor extends Actor with ActorLogging{
        var child:ActorRef = context.actorOf(Props[MyActor], name = "myActor")
        def receive = {
            case "stop" => context.stop(child)
            case x => {
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
    val systemLog = system.log

    val firstActor = system.actorOf(Props[FirstActor], name = "firstActor")

    systemLog.info("ready to send message to firstActor")

    firstActor!"test"
    firstActor! 123
    firstActor!"stop"

    // stop method will not stop process, but terminate will
    Thread.sleep(3000)
    system.terminate()
}
