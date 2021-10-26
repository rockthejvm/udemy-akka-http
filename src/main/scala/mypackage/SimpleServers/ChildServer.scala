package mypackage.SimpleServers

import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, get, path}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.collection.mutable._
import scala.concurrent.{Await, Future}

case class Child(name:String)
class Tution extends Actor{
  var children:ArrayBuffer[Child]=ArrayBuffer[Child]()
  def receive = {
    case Child(name)=>children.append(Child(name))
    case "remove"=>children.remove(0)
    case "all"=>sender ! children
  }
}
object ChildServer extends App {
  implicit val system = ActorSystem("ChildServer")
  val actor = system.actorOf(Props[Tution],name = "ChildServer" )
  import akka.pattern.ask
  import akka.util.Timeout

  import scala.concurrent.duration._
    implicit  val timeout=Timeout(2 seconds)

  implicit val materializer = ActorMaterializer()
  import system.dispatcher
  val chainedRoute: Route = path("myEndpoint") {
                              get {
                                val children:Future[Any]= actor ? "all"
                                val result:ArrayBuffer[Child] = Await.result(children, timeout.duration).asInstanceOf[ArrayBuffer[Child]]
                                print(result.length)
                                complete(StatusCodes.OK)
                              }
                            }
  Http().bindAndHandle(chainedRoute, "localhost", 8080)
}