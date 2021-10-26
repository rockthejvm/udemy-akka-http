package mypackage.SimpleActor

import akka.actor.{Actor, ActorSystem, Props}

import scala.collection.mutable._
import scala.concurrent.{Await, Future}

case class Student(name:String)
class School extends Actor{
  var students:ArrayBuffer[Student]=ArrayBuffer[Student]()
  def receive = {
    case Student(name)=>students.append(Student(name))
    case "remove"=>students.remove(0)
    case "all"=>sender ! students
    case "test"=>sender ! Student("test")
  }
}
object StudentActor extends App {
  val system = ActorSystem("StudentServer")
  val actor = system.actorOf(Props[School],name = "StudentServer" )
  actor ! Student("deepak") //tell will not send anything back

  import akka.pattern.ask
  import akka.util.Timeout

  import scala.concurrent.duration._
  implicit  val timeout=Timeout(2 seconds)
  val students:Future[Any]= actor ? "all"
  val result:ArrayBuffer[Student] = Await.result(students, timeout.duration).asInstanceOf[ArrayBuffer[Student]]
  system.terminate()
  print(result.length)
}