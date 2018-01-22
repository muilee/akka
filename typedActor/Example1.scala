package typedActor

import akka.actor.ActorSystem
import akka.event.Logging
import scala.concurrent.{ Promise, Future }
import akka.actor.{ TypedActor, TypedProps }
import scala.concurrent.duration._
import scala.concurrent.Await
object Example1 extends App {
    trait Squarer {
        //fire-and-forget消息
        def squareDontCare(i: Int): Unit
        //非阻塞send-request-reply消息
        def square(i: Int): Future[Int]
        //阻塞式的send-request-reply消息
        def squareNowPlease(i: Int): Option[Int]
        //阻塞式的send-request-reply消息
        def squareNow(i: Int): Int
    }

    class SquarerImpl(val name: String) extends Squarer {
        def this() = this("SquarerImpl")

        def squareDontCare(i: Int): Unit = i * i
        def square(i: Int): Future[Int] = Promise.successful(i * i).future
        def squareNowPlease(i: Int): Option[Int] = Some(i * i)
        def squareNow(i: Int): Int = i * i
    }

    val system = ActorSystem("TypedActorSystem")
    val log = Logging(system, this.getClass)

    val mySquarer: Squarer = TypedActor(system).typedActorOf(TypedProps[SquarerImpl](),"mySquarer")
    val otherSquarer: Squarer = TypedActor(system).typedActorOf(TypedProps(classOf[Squarer], new SquarerImpl("SquarerImpl")), "otherSquarer")

    mySquarer.squareDontCare(10)

    val oSquare = mySquarer.squareNowPlease(10)

    log.info("oSquare="+oSquare)

    val iSquare = mySquarer.squareNow(10)
    log.info("iSquare="+iSquare)

    val fSquare = mySquarer.square(10)
    val result = Await.result(fSquare, 5 second)

    log.info("fSquare="+result)

    system.terminate()
}
