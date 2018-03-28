package akka.router.hash

import java.util.Date

import akka.actor.{Actor, ActorSystem, Props}
import akka.router.hash.MoneyCounter.{OneHand, ReportTotal}
import akka.routing.ConsistentHashingPool

/**
  * Created by GongWenBo on 2018/1/30.
  */

object MoneyCounter {

  sealed trait Counting

  case class OneHand(cur: String, amount: Double) extends Counting

  case class ReportTotal(cur: String) extends Counting

}


class MoneyCounter extends Actor {
  var currency: String = "RMB"
  var amount: Double = 0

  override def receive: Receive = {
    case OneHand(cur, amt) =>
      println(s"${self.path.name} ......$cur........$amt")
      currency = cur
      amount += amt
    case ReportTotal(_) =>
      println(s"$currency...$amount")
  }
}

object MoneyRunner extends App {
  val system = ActorSystem("system")

  //定义mapping函数
  def mcHashMapping: PartialFunction[Any, Any] = {
    case OneHand(cur, _) => cur
    case ReportTotal(cur) => cur
  }

  val actor = system.actorOf(ConsistentHashingPool(
    nrOfInstances = 5, hashMapping = mcHashMapping, virtualNodesFactor = 3)
    .props(Props(new MoneyCounter)), "moneyCounter")

  actor ! OneHand("RMB", 100)
  actor ! OneHand("USD", 1)
  actor ! OneHand("HKD", 10)
  actor ! OneHand("USD", 1)
  actor ! OneHand("RMB", 100)
  actor ! OneHand("HKD", 10)

  actor ! ReportTotal("HKD")
  actor ! ReportTotal("USD")

  scala.io.StdIn.readLine()
  system.terminate()

}

