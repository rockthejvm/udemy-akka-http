package mypackage
import spray.json._

sealed case class Person(name:String,age:Int)

//Mandatory class to use for spraj Json
trait PersonJsonJsonProtocl extends DefaultJsonProtocol{ implicit val personFormat=jsonFormat2(Person) } //jsonFormat2 = Constructor takes 2 arguemts
object SprayJsontest extends App with PersonJsonJsonProtocl {
  //marshal
  val simpleGuitar = Person("Fender", 50)
  println(simpleGuitar.toJson.prettyPrint)

  // unmarshalling
  val simpleGuitarJsonString =
    """
      |{
      |  "name": "Fender",
      |  "age": 3
      |}
    """.stripMargin
  println(simpleGuitarJsonString.parseJson.convertTo[Person])
}
