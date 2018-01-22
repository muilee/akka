package akkaTest

import akka.actor.{Actor, ActorSystem, Props, ActorLogging, ActorRef}
object AkkaExample8 extends App {

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
        override def receive: Receive = {
            case "test" => log.info("received test")
            case _      => log.info("received unknown message")
        }
    }

    val system = ActorSystem("MyActorSystem")
    val systemLog = system.log

    val firstActor = system.actorOf(Props[FirstActor], name="firstActor")

    val firstActorPath = system.child("firstActor")
    val myActorPath = system.child("firstActor").child("myActor")
    systemLog.info("firstActorPath--->{}", firstActorPath)

    // get ActorRef using ActorPath
    val firstActorUsingActorPath  = system.actorSelection(firstActorPath)
    // get ActorRef absolute path
    val firstActorUsingAbsolutePath  = system.actorSelection("akka://MyActorSystem/user/firstActor")
    // get ActorRef relative path
    val firstActorUsingRelativePath  = system.actorSelection("user/firstActor")

    systemLog.info("ready to send message to firstActor")

    firstActorUsingAbsolutePath ! "test"
    firstActorUsingAbsolutePath ! 123

    println("------------------------")

    firstActorUsingRelativePath ! "test"
    firstActorUsingRelativePath ! 123
    Thread.sleep(3000)
    system.terminate()
}
