package mypackage.SimpleServers

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.server.Directives.{complete, failWith, onComplete}
import akka.stream.ActorMaterializer
import org.json.JSONObject
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{Failure, Success}

object ServerLowLevelAsync extends  App {

  implicit val system = ActorSystem("LowLevelServerAPI")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher
  val html=    """<html><body>Hello from Akka HTTP!</body></html>""".stripMargin
  val htmlerror="""html><body>OOPS! The resource can't be found.</body></html>""".stripMargin

  val response = HttpEntity(ContentTypes.`text/html(UTF-8)`,html)
  val response2 = HttpEntity(ContentTypes.`text/html(UTF-8)`,htmlerror)

  val jsonObject: JSONObject = new JSONObject();
  jsonObject.put("name", "deepak");
  jsonObject.put("address", "Bangalore");
  val jsonresponse = HttpEntity(ContentTypes.`application/json`, jsonObject.toString)

  // method, URI, HTTP headers, content and the protocol (HTTP1.1/HTTP2.0) -- decompose
  val asyncRequestHandler: HttpRequest => Future[HttpResponse] = {
                                case HttpRequest(HttpMethods.GET, Uri.Path("/home"), _, _, _) => Future(HttpResponse(entity = response))
                                case HttpRequest(HttpMethods.GET, Uri.Path("/search"), _, _, _) => Future(HttpResponse(StatusCodes.Found,
                                                                                                                        headers = List(Location("http://google.com"))))
                                case HttpRequest(HttpMethods.POST, Uri.Path("/json"), _, entity, _) =>
                                  val strictEntityFuture = entity.toStrict(2 seconds) //convert to future with wait of 2 sec
                                  val jsonFuture = strictEntityFuture.map(_.data.utf8String) //Byte stream to String
                                  onComplete(jsonFuture){
                                    case Success(jsondata) =>
                                                            val j:JSONObject=new JSONObject(jsondata)
                                                              println(j.get("name").toString)
                                                              complete(StatusCodes.OK)
                                    case Failure(ex) =>   complete(StatusCodes.InternalServerError)
                                  }
                                    Future(HttpResponse(StatusCodes.Found)  )

                                case request: HttpRequest => {request.discardEntityBytes()
                                                              Future(HttpResponse(StatusCodes.NotFound, entity = response2)) //StatusCodes.OK,
                                                              }
  }
  Http().bindAndHandleAsync(asyncRequestHandler, "localhost", 8081)
}
