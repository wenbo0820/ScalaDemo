package akka.cluster.singleton

import akka.actor.{ActorIdentity, ActorPath, ActorSystem, Identify, Props}
import akka.cluster.Cluster
import akka.persistence.PersistentActor
import akka.persistence.journal.leveldb.{SharedLeveldbJournal, SharedLeveldbStore}
import akka.util.Timeout
import akka.pattern._
import akka.remote.ContainerFormats.ActorIdentity

import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory

/**
  * Created by GongWenBo on 2018/2/7.
  */
sealed trait Command

case object Dig extends Command

case object Plant extends Command

case object AckDig extends Command

case object AckPlant extends Command

case object Disconnect extends Command

case object CleanUp extends Command

sealed trait Event

case object AddHole extends Event

case object AddTree extends Event

case class State(nHoles: Int, nTrees: Int, nMatches: Int)

object SingletonActor {

  def create(port: Int) = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=${port}")
      .withFallback(ConfigFactory.parseString("akka.cluster.roles = [singleton]"))
      .withFallback(ConfigFactory.load("cluster.conf").getConfig("singleton"))

    val singleSystem = ActorSystem("singletonClusterSystem", config)
    startupSharedJournal(singleSystem,(port == 2551),ActorPath.fromString("akka.tcp://singletonClusterSystem/user/store"))

    //TODO

  }

  def startupSharedJournal(system: ActorSystem,startStore: Boolean,path: ActorPath)={
    if (startStore)
      system.actorOf(Props[SharedLeveldbStore],"store")
    import system.dispatcher
    implicit val timeout = Timeout(15 seconds)
//    val f = (system.actorSelection(path) ? Identify(None))
//    f.onSuccess{
//      case ActorIdentity(_,Some(ref)) =>
//        SharedLeveldbJournal.setStore(ref,system)
//      case _ =>
//        system.log.error("Shared journal not started at {}",path)
//        system.terminate()
//    }
//    f.onFailure{
//      case _=>
//        system.log.error("Lookup of shared journal at {} timed out", path)
//        system.terminate()
//    }
  }

}


class SingletonActor extends PersistentActor {
  val cluster = Cluster(context.system)

  var freeHoles = 0


  override def receiveRecover: Receive = {
    case _=>
  }

  override def receiveCommand: Receive = {
    case _=>
  }

  override def persistenceId: String = {
   "aa"
  }
}
