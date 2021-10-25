package mypackage

import akka.actor.{Actor, ActorSystem, Props}

/*An actor runs in parallel to the main application thread,
Can only communicate with Actor by sending messages
Members of Actors cannot be reached
If there are no more messages, the actor waits
Body of Actor is just the receive method  contains only case Statements*/
class HelloActor extends Actor {
  def receive = {
    case s:String => println(s"you said '$s'")
    case _ => println("huh?")
  }}
object SimpleActor extends App {
  val system = ActorSystem("HelloSystem")  // an actor needs an ActorSystem
  val helloActor = system.actorOf(Props[HelloActor],name = "helloActor")// create and start the actor
  helloActor ! "hello" // send the actor two known messages
  helloActor ! 1 // send it an unknown message
  system.terminate()// shut down the system
}
//output:
//you said 'hello'
//huh?