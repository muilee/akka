package akkaSendMessage

import akka.actor.{Actor, Props, ActorSystem}
import akka.event.Logging
import scala.concurrent.Future
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import akka.pattern.pipe
import scala.concurrent.ExecutionContext.Implicits.global

object SendAndReceiveFuture extends App {
    case class BasicInfo(id:Int, val name:String, age:Int)
    case class InterestInfo(id:Int, val interest:String)
    case class Person(basicInfo: BasicInfo, interestInfo: InterestInfo)

    class BasicInfoActor extends Actor {
        val log = Logging(context.system, this)
        override def receive: Receive = {
            case id: Int => log.info("id=" + id); sender ! new BasicInfo(id,"John",19)
            case _     => log.info("received unknown message")
        }
    }

    class InterestInfoActor extends Actor {
        val log = Logging(context.system, this)
        override def receive: Receive = {
            case id: Int => log.info("id=" + id); sender ! new InterestInfo(id,"football")
            case _     => log.info("received unknown message")
        }
    }

    class PersonActor extends Actor {
        val log = Logging(context.system, this)
        def receive = {
            case person: Person => log.info("Person=" + person)
            case _      ⇒ log.info("received unknown message")
        }
    }

    class CombineActor extends Actor {
        implicit val timeout = Timeout(5 seconds)
        val basicInfoActor = context.actorOf(Props[BasicInfoActor], name="BasicInfoActor")
        val interestInfoActor = context.actorOf(Props[InterestInfoActor], name="InterestInfoActor")
        val personActor = context.actorOf(Props[PersonActor], name="PersonActor")
        override def receive: Receive = {
            case id: Int =>
                val combineResult: Future[Person] =
                    for {
                        //向basicInfoActor發送Send-And-Receive-Future消息，mapTo方法將返回结果映射為BasicInfo類型
                        basicInfo <- ask(basicInfoActor, id).mapTo[BasicInfo]  // ask return Future[Any]

                        //向interestInfoActor發送Send-And-Receive-Future消息，mapTo方法將返回结果映射為InterestInfo類型
                        interestInfo <- ask(interestInfoActor, id).mapTo[InterestInfo]
                    } yield Person(basicInfo, interestInfo)

                //將Future结果發送给PersonActor
                // 另一種寫法 combineResult pipeTo personActor
                pipe(combineResult).to(personActor)


        }
    }
    val system = ActorSystem("Send-And-Receive-Future")
    val combineActor = system.actorOf(Props[CombineActor],name="CombineActor")
    combineActor ! 12345
    combineActor ! 22222

    Thread.sleep(3000)
    system.terminate()


}
