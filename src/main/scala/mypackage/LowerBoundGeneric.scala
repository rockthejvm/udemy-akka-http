//https://medium.com/nerd-for-tech/futures-in-scala-201677bc5d97

package mypackage

trait A
class B extends A
class C extends B
object LowerBoundGeneric extends App {
  class Test[A >: B](val x: A) //Can have of type A and B not C
  val temp = new B() // new C() = Fail
  val test: Test[B] = new Test[B](temp)
}
object CovariantGeneric extends App {
  class Test2[+A]{ def run[B >: A](element: B)=print("working") }
  val temp2 =new C() // new C() = Fail
  new Test2[B]().run(temp2)
}
//whereby the compiler converts f(a) into f.apply(a)
object Applytest extends App{
  class Foo(x: Int) { def apply(y: Int) =x+y}
  val f = new Foo(3)
  println(f(4))  // returns 25
}
/*
function  is f: X -> Y,
A partial function =  Does not force f to map every element of X to an element of Y
ie., several subpartial function to handle differnt elements in same data set
new PartialFunction[input , output]
if "isDefined" is true than execute "apply"
orElse, andthen
 */
object Partialtest extends App{
  val sample = 1 to 5
    val isEven = new PartialFunction[Int, String] {
      def apply(x: Int) = x + " is even"
      def isDefinedAt(x: Int) = (x != 0 && x%2 == 0)
    }
  val isOdd: PartialFunction[Int, String] = {
       case x if x % 2 == 1 => x + " is odd"
    }
  val evenNumbers = sample map (isEven orElse isOdd)
  print(evenNumbers)
}
/*
Companion object and its class can access each other’s private members (fields and methods)
Have same name
Same file
 */
object CompanionTest extends App{
  class Person {var name = ""}
  object Person {
    def apply(name: String): Person = {
      var p = new Person()
      p.name = name
      p
    }
  }
  print(Person("Fred Flinstone").name) //Person.apply("Fred Flinstone").
}
/*
Anything inside Future {}, is run in a different thread
Application’s main thread doesn’t stop for Future to Complete
Result of Future is always  Try types: Success or Failure
To make main thread wait scala.concurrent.Await.result(future,15.seconds) is used
isComplete , value ,map , collect
 */
object FutureTest extends App{
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.Future
  import scala.util.{Failure, Success}

  val f1:Future[Int] = Future { Thread.sleep(500); 21 + 21 } //Normal
  while(f1.isCompleted!=true){println("future operation completed ?? -  "+f1.isCompleted)}
  println(f1.value)

  val res= f1.map(i => i+1) //Map
  Thread.sleep(200)
  println(res)

  val result: Future[(Int, Int)] = for { //For
    res1 <- Future { Thread.sleep(10); 1 }
    res2 <- Future { Thread.sleep(20); 1 }
  } yield (res1, res2)

  Thread.sleep(200)
  result.onComplete { //Case
    case Success(value) => println(s"Got the callback, value = $value")
    case Failure(e) => e.printStackTrace
  }
}

object ImplicitTest extends App{
  case class Person(name: String) {def greet = println(s"Hi, my name is $name")}
  implicit def fromStringToPerson(name: String) = Person(name)
  "Peter".greet
}





