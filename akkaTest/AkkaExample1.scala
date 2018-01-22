package akkaTest

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem

object AkkaExample1 extends App {


    class StringActor extends Actor {
        val log = Logging(context.system, this)
        override def receive: Receive = {
            case s:String => log.info("received message:" + s)
            case _ => log.info("received unknown message")
        }
    }

    val system = ActorSystem("StringSystem")
    val stringActor = system.actorOf(Props[StringActor], name="StringActor" )
    stringActor ! "Creating Actors with default constructor"
    system.terminate()
}
