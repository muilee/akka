package akkaTest

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem

object AkkaExample3 extends App {

    class StringActor extends Actor {
        val log = Logging(context.system, this)
        override def receive: Receive = {
            case s:String => log.info("received message:" + s)
            case _ => log.info("received unknown message")
        }
    }

    class ContextActor extends Actor {
        val log = Logging(context.system, this)

        // use context to create child actor
        var stringActor = context.actorOf(Props[StringActor], name="StringActor")
        override def receive: Receive = {
            case s:String => log.info("received message:" + s); stringActor ! s
            case _ => log.info("received unknown message")
        }
    }

    val system = ActorSystem("StringSystem")
    val contextActor = system.actorOf(Props[ContextActor], name="ContextActor" )
    contextActor ! "Creating Actors with implicit val context"
    system.terminate()
}
