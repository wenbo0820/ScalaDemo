package akka.cluster

import akka.actor._
import akka.cluster.ClusterEvent.MemberUp
import akka.cluster.Messages._
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
/**
  * Created by GongWenBo on 2018/2/6.
  */
object Messages {

  sealed trait MathOps
  case class Add(x: Int, y: Int) extends MathOps
  case class Sub(x: Int, y: Int) extends MathOps
  case class Mul(x: Int, y: Int) extends MathOps
  case class Div(x: Int, y: Int) extends MathOps

  sealed trait ClusterMsg
  case class RegisterBackEndActor(role: String) extends ClusterMsg
}

object CalcFunctions{
  def propsFunc = Props(new CalcFunctions)
  def propsSuper(role:String) = Props(new CalculatorSupervisor(role))
}

class CalcFunctions extends Actor{
  override def receive: Receive = {
    case Add(x,y) => println(s"$x + $y  = ${x+y} by $self ")
    case Sub(x,y) => println(s"$x - $y  = ${x-y} by $self ")
    case Mul(x,y) => println(s"$x * $y  = ${x*y} by $self ")
    case Div(x,y) => println(s"$x / $y  = ${x/y} by $self ")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println(s"Restarting calculator : ${reason.getCause}")
    super.preRestart(reason,message)
  }
}

class CalculatorSupervisor(mathOps:String) extends Actor{
  def decider : PartialFunction[Throwable,SupervisorStrategy.Directive] = {
    case _ : ArithmeticException => SupervisorStrategy.resume
  }
  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 5,withinTimeRange = 5 seconds){
      decider.orElse(SupervisorStrategy.defaultDecider)
    }

  val calcActor = context.actorOf(CalcFunctions.propsFunc,"calcFunction")
  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self,classOf[MemberUp])
    super.preStart()
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
    super.postStop()
  }

  override def receive: Receive = {
    case MemberUp(member)=>
      if (member.hasRole("frontend")){
        context.actorSelection(RootActorPath(member.address) + "/user/frontend") ! RegisterBackEndActor(mathOps)
      }
    case msg@_ =>
        calcActor forward msg
  }
}


object Calculator{
  def create(role : String) ={
    val config = ConfigFactory.parseString("backend.akka.cluster.roles = [\""+role+"\"]")
                  .withFallback(ConfigFactory.load("cluster.conf").getConfig("backend"))
    val calcSystem = ActorSystem("calcClusterSystem",config)
    val calcRef = calcSystem.actorOf(CalcFunctions.propsSuper(role),"calculator")
  }
}

object CalcRouter {
  def  props = Props(new CalcRouter)
}
class CalcRouter extends Actor{
  var nodes :Map[String,ActorRef] = Map.empty
  override def receive: Receive = {
    case RegisterBackEndActor(role) =>
      nodes += (role -> sender())
      context.watch(sender())
    case add:Add => runCommand("adder",add)
    case sub:Sub => runCommand("substractor",sub)
    case mul:Mul => runCommand("multiplier",mul)
    case div:Div => runCommand("divider",div)

    case Terminated(ref) =>
      nodes = nodes filter(_._2 != ref)
  }

  def runCommand(role:String,ops : MathOps)={
    nodes.get(role) match {
      case Some(ref) => ref ! ops
      case None => println(s"$role is not registered")
    }
  }
}

object FrontEnd{
  private var router : ActorRef =_
  def create ={
    val calcSystem = ActorSystem("calcClusterSystem",ConfigFactory.load("cluster.conf").getConfig("frontend"))
    router = calcSystem.actorOf(CalcRouter.props,"frontend")
  }
  def getRouter = router
}

object Demo extends App{
  FrontEnd.create   //启动seed-node

  Calculator.create("adder")
  Calculator.create("substractor")
  Calculator.create("multiplier")
  Calculator.create("divider")
  Thread.sleep(2000)

  val router = FrontEnd.getRouter

  router ! Add(10,3)
  router ! Mul(3,7)
  router ! Div(8,2)
  router ! Sub(45, 3)
  router ! Div(8,0)
  router ! Div(8,1)
}