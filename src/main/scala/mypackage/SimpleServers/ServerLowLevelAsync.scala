package mypackage.SimpleServers

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Location
import akka.stream.ActorMaterializer

import scala.concurrent.Future

object ServerLowLevelAsync extends  App {

  implicit val system = ActorSystem("LowLevelServerAPI")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher
  val html=    """<html><body>Hello from Akka HTTP!</body></html>""".stripMargin
  val htmlerror="""html><body>OOPS! The resource can't be found.</body></html>""".stripMargin

  val response = HttpEntity(ContentTypes.`text/html(UTF-8)`,html)
  val response2 = HttpEntity(ContentTypes.`text/html(UTF-8)`,htmlerror)

  // method, URI, HTTP headers, content and the protocol (HTTP1.1/HTTP2.0) -- decompose
  val asyncRequestHandler: HttpRequest => Future[HttpResponse] = {
                                case HttpRequest(HttpMethods.GET, Uri.Path("/home"), _, _, _) => Future(HttpResponse(entity = response))
                                case HttpRequest(HttpMethods.GET, Uri.Path("/search"), _, _, _) => Future(HttpResponse(StatusCodes.Found,
                                                                                                                        headers = List(Location("http://google.com"))))
                                case request: HttpRequest => {request.discardEntityBytes()
                                                              Future(HttpResponse(StatusCodes.NotFound, entity = response2)) //StatusCodes.OK,
                                                              }
  }
  Http().bindAndHandleAsync(asyncRequestHandler, "localhost", 8081)
}
