package akkaTest

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem

object AkkaExample2 extends App {

    class StringActor(var name:String) extends Actor {
        val log = Logging(context.system, this)
        override def receive: Receive = {
            case s:String => log.info("received message:" + s)
            case _ => log.info("received unknown message")
        }
    }

    val system = ActorSystem("StringSystem")
    val stringActor = system.actorOf(Props(new StringActor("StringActor")), name="StringActor" )
    stringActor ! "Creating Actors with non-default constructor"
    system.terminate()
}
