package akka.actor
import java.util.concurrent.TimeUnit

import akka.actor.Actor.Receive
import akka.pattern._
import akka.util.Timeout

/**
  * Created by GongWenBo on 2018/2/9.
  */
object ActorIdentifyDemo  {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("actorIdentifySystem")
    system.actorOf(Props(new ReceiveActor).withDispatcher("profile-dispatcher"),"receive")
    Thread.sleep(1000)
    val other =system.actorOf(Props(new OtherActor).withDispatcher("profile-dispatcher"),"other")

    val sender = system.actorOf(Props(new SenderActor),"sender")
    sender ! "send"
    Thread.sleep(1000)
    system.terminate()
  }



}

class OtherActor extends Actor{

  @volatile var running = true

  def run(): Unit ={
    val time = System.currentTimeMillis() + 5000
    while (running){
        if ( time < System.currentTimeMillis())
          running = false
    }
  }
  println("other run")
  run()

  override def receive: Receive = {
    case _=>
      running = false
  }

  override def postStop(): Unit = {
    super.postStop()
    running = false
  }
}

class ReceiveActor extends Actor{
  override def receive: Receive = {
    case "answer"=>
      sender() ! "bbbbbbb"
    case _ =>
      println("aaaaa")
  }
}
class SenderActor extends Actor with ActorLogging{
  import context.dispatcher
  implicit val timeout = Timeout(10,TimeUnit.SECONDS)
  override def receive: Receive = {
    case "send" =>
      log.info("sender")
      context.system.actorSelection("/user/receive") ! Identify()
    case ActorIdentity(_,Some(ref))=>
      println("Receive actor identify message")
      println(ref.path)
    case "answer" =>
      context.actorSelection("/user/receive") ? "answer" foreach(
        a =>
          println(a)
      )
    case ActorIdentity(_,None)=>
      println("not actor found")
    case a=>
      println(a)
  }
}
