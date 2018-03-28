package akka.actor

import scala.concurrent.duration._
/**
  * Created by GongWenBo on 2018/2/9.
  */
object LifeCycleDemo extends App {
    val system = ActorSystem("lifeCycleSystem")
    val parent  = system.actorOf(Props(new LifCycleParent),"parent")
    parent ! "exception"
    Thread.sleep(1000)
    system.terminate()
}

class LifCycleParent extends Actor {

  def decider : PartialFunction[Throwable,SupervisorStrategy.Directive] = {
    case _ : ArithmeticException => SupervisorStrategy.restart
  }
  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 5,withinTimeRange = 5 seconds){
      decider.orElse(SupervisorStrategy.defaultDecider)
    }

  var child: ActorRef = _

  override def receive: Receive = {
    case "stop" =>
      context.stop(self)
    case msg =>
      child ! msg
  }

  override def preStart(): Unit = {
    println("parent preStart")
    child = context.actorOf(Props(new LifeCycleChild), "child")
    super.preStart()
  }

  override def postStop(): Unit = {
    println("parent post stop")
    super.postStop()
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println("parent pre restart")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit = {
    println("parent post restart")
    super.postRestart(reason)
  }
}

class LifeCycleChild extends Actor {

  override def receive: Receive = {
    case "exception"=>
      throw new ArithmeticException("aaaaaaaaa")
  }

  override def preStart(): Unit = {
    println("child preStart")
    super.preStart()
  }

  override def postStop(): Unit = {
    println("child postStop")
    super.postStop()
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println("child pre Restart")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit = {
    println("child post restart")
    super.postRestart(reason)
  }
}
