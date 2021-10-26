package mypackage.SimpleServers

import akka.actor.ActorSystem
//import akka.actor.Status.{Failure, Success}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.concurrent.duration._
import scala.util.{Failure, Success}


object ServerDirectives extends App {
  implicit val system = ActorSystem("UsingDirectives")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher
val html:String= """ <html><body>Hello</body></html>""".stripMargin
  // equivalent directives for get, put, patch, delete, head, options
val chainedRoute: Route = path("myEndpoint") {get {complete(StatusCodes.OK)} ~ //note tidle is used
                                                    post {complete(StatusCodes.Forbidden)}
                                                    } ~
                            path("home") {complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,html) ) } ~
                            path("api" / "order" / IntNumber / IntNumber) {
                                               (id, inventory) =>println(s"I've got TWO numbers in my path: $id, $inventory")
                                                complete(StatusCodes.OK)
                                                } ~
                            path("api" / "item") {// /api/item?id=45
                                                         parameter('id.as[Int]) { (itemId: Int) =>println(s"I've extracted the ID as $itemId")
                                                        complete(StatusCodes.OK)
                              }} ~
                            path("notSupported") {
                                                        failWith(new RuntimeException("Unsupported!")) // completes with HTTP 500
                                                      } ~
  path("post") { (post & pathEndOrSingleSlash & extractRequest & extractLog) { (request, log) =>
                                                      val entity = request.entity
                                                      val strictEntityFuture = entity.toStrict(2 seconds)
                                                      val personFuture = strictEntityFuture.map(_.data.utf8String)
                                                      onComplete(personFuture) {
                                                        case Success(person) =>
                                                          log.info(s"Got person: $person")
                                                          complete(StatusCodes.OK)
                                                        case Failure(ex) =>
                                                          failWith(ex)
                                                      }}}~
                              path("index") { completeOkRoute  }

val repeatedRoute =   path("about") {  complete(StatusCodes.OK)  } ~
                      path("aboutUs") { complete(StatusCodes.OK)  }

  val dryRoute = //since same complete(StatusCodes.OK) we can use or
                  (path("about") | path("aboutUs")) { complete(StatusCodes.OK)  }

  val completeOkRoute = complete(StatusCodes.OK)

  Http().bindAndHandle(chainedRoute, "localhost", 8080)

}