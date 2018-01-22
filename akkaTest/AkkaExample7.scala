package akkaTest

import akka.actor.{Actor, ActorSystem, Props, ActorLogging, ActorRef}
object AkkaExample7 extends App {

    class FirstActor extends Actor with ActorLogging {
        var child: ActorRef = _
        override def preStart(): Unit = {
            log.info("preStart() in FirstActor")
            // build child actor using context
            child = context.actorOf(Props[MyActor], name = "myActor")
        }

        override def receive: Receive = {
            // send message to child actor (MyActor)
            case x => child ! x; log.info("received " + x)
        }
    }

    class MyActor extends Actor with ActorLogging {
        var parent: ActorRef = _
        override def preStart(): Unit = {
            // get parent ActorRef
            parent = context.parent
        }

        override def receive: Receive = {
            case "test" => log.info("received test"); parent ! "message from ParentActorRef"
            case "message from ParentActorRef" => log.info("message from ParentActorRef")
            case _ => log.info("received unknown message")
        }
    }

    val system = ActorSystem("MyActorSystem")
    val systemLog = system.log

    // build firstActor ActorRef
    val firstActor = system.actorOf(Props[FirstActor], name = "firstActor")
    // get firstActor path
    val firstActorPath = system.child("firstActor")
    println(firstActorPath)

    // get firstActor ActorRef using system.actorSelection method
    val firstActorUsingActorPath = system.actorSelection(firstActorPath)
    systemLog.info("ready to send message to firstActor")

    firstActorUsingActorPath ! "test"
    firstActorUsingActorPath ! 123
    Thread.sleep(3000)
    system.terminate()
}
