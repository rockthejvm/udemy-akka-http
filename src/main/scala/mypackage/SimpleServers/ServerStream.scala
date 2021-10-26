package mypackage.SimpleServers

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow

object ServerStream extends App{
  implicit val system = ActorSystem("LowLevelServerAPI")
  implicit val materializer = ActorMaterializer()

  val html=    """<html><body>Hello from Akka HTTP!</body></html>""".stripMargin
  val htmlerror="""html><body>OOPS! The resource can't be found.</body></html>""".stripMargin

  val response = HttpEntity(ContentTypes.`text/html(UTF-8)`,html)
  val response2 = HttpEntity(ContentTypes.`text/html(UTF-8)`,htmlerror)
  //HttpRequest(HttpMethods.GET, Uri.Path("/home"), _, _, _) = method, URI, HTTP headers, content and the protocol (HTTP1.1/HTTP2.0) called decompose
  val streamsBasedRequestHandler: Flow[HttpRequest, HttpResponse, _] = Flow[HttpRequest].map {
                                case HttpRequest(HttpMethods.GET, Uri.Path("/home"), _, _, _) => HttpResponse(entity = response)
                                case request: HttpRequest => {request.discardEntityBytes()
                                                              HttpResponse(StatusCodes.NotFound, entity = response2) //StatusCodes.OK,
                                                            }
  }
  Http().bindAndHandle(streamsBasedRequestHandler, "localhost", 8082)
}


