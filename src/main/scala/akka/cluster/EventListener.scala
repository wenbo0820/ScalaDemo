package akka.cluster

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.cluster.ClusterEvent._
import com.typesafe.config.ConfigFactory

/**
  * Created by GongWenBo on 2018/2/6.
  */
class EventListener extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
    super.preStart()
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
    super.postStop()
  }

  override def receive: Receive = {
    case MemberJoined(member) =>
      log.info(s"Member is joining : {${member.address}}")
    case MemberLeft(member) =>
      log.info(s"Member is leaving : {${member.address}}")
    case MemberUp(member) =>
      log.info(s"Member is up : {${member.address}}")
    case MemberExited(member) =>
      log.info(s"Member is exiting : {${member.address}}")
    case MemberRemoved(member, previousStatus) =>
      log.info(s"Member is removed : {${member.address}}, previous status : {$previousStatus}")
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
      cluster.down(member.address)
    case _: MemberEvent => // ignore
  }

}

object ClusterDemoRunner extends App{
  val port = 2551
  val addr = 2551
  val seedNodeSetting = "akka.cluster.seed-nodes = [\"akka.tcp://clusterSystem@127.0.0.1:" + s"${addr}" +"\"]"

  val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port = $port")
                .withFallback(ConfigFactory.parseString(seedNodeSetting))
                .withFallback(ConfigFactory.load("cluster.conf"))

  val clusterSystem = ActorSystem("clusterSystem",config)
  val eventListener = clusterSystem.actorOf(Props[EventListener],"eventListener")

  val cluster  = Cluster(clusterSystem)
  cluster.registerOnMemberRemoved(println("Leaving cluster ,I should clean up ..."))
  cluster.registerOnMemberUp(println("Hookup to cluster. Do some setups ..."))
  println("actor system started !")
  scala.io.StdIn.readLine()
  clusterSystem.terminate()
}


object ClusterDemoRunner2 extends App{
  val port = 0
  val addr = 2551
  val seedNodeSetting = "akka.cluster.seed-nodes = [\"akka.tcp://clusterSystem@127.0.0.1:" + s"${addr}" +"\"]"

  val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port = $port")
    .withFallback(ConfigFactory.parseString(seedNodeSetting))
    .withFallback(ConfigFactory.load("cluster.conf"))

  val clusterSystem = ActorSystem("clusterSystem",config)
  val eventListener = clusterSystem.actorOf(Props[EventListener],"eventListener")

  val cluster  = Cluster(clusterSystem)
  cluster.registerOnMemberRemoved(println("Leaving cluster ,I should clean up ..."))
  cluster.registerOnMemberUp(println("Hookup to cluster. Do some setups ..."))
  println("actor system started !")
  scala.io.StdIn.readLine()
  clusterSystem.terminate()
}
