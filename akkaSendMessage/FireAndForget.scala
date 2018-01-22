package akkaSendMessage

import akka.actor.{Actor, Props, ActorSystem}
import akka.event.Logging

object FireAndForget extends App {
    case class Start(var msg:String)
    case class Run(var msg:String)
    case class Stop(var msg:String)

    class ExampleActor extends Actor {
        val other = context.actorOf(Props[OtherActor], name="OtherActor")
        val log = Logging(context.system, this)
        override def receive: Receive = {
            // use implicit method send sender
            case Start(msg) => println(sender); other ! msg
            // use explicit method send sender
            case Run(msg) => sender ! msg; other.tell(msg, sender)
        }
    }

    class OtherActor extends Actor {
        val log = Logging(context.system, this)
        override def receive: Receive = {
            case s: String => log.info("received message:\n" + s)
            case _      ⇒ log.info("received unknown message")
        }
    }

    val system = ActorSystem("MessageProcessingSystem")
    val exampleActor = system.actorOf(Props[ExampleActor], name="ExampleActor")

    exampleActor ! Run("Running")
    exampleActor ! Start("Starting")
    Thread.sleep(3000)
    system.terminate()

}
