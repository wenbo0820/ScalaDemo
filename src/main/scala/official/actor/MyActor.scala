package official.actor

import akka.actor.{Actor, ActorSystem, Props}

/**
  * Created by GongWenBo on 2018/3/21.
  */
object MyActor extends App{
  val system = ActorSystem("myActorSystem")
  val ref = system.actorOf(Props(classOf[MyActor],1))
  ref ! "hello"
  Thread.sleep(1000)
  system.terminate()
}

class MyActor(a: Int) extends Actor {
  override def receive: Receive = {
    case a => println(a)
  }
}
