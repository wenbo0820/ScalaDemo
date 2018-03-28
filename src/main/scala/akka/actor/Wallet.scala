package akka.actor

import akka.dispatch.{PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.{Config, ConfigFactory}

/**
  * Created by GongWenBo on 2018/1/30.
  */
class Wallet extends Actor {
  var balance: Double = 0
  var isOpen: Boolean = false

  import akka.actor.Wallet._

  override def receive: Receive = {
    case Open =>
      isOpen = true
      println("Open the wallet")
    case Close =>
      isOpen = false
      println("Close the wallet")
    case Total =>
      println(s"Total : $balance")
    case Save(amount) =>
      if (isOpen) {
        balance += amount
        println(s"Save $amount")
      } else {
        self ! Open
        self ! Save(amount)
      }
    case Get(amount) =>
      if (isOpen) {
        if (balance >= amount) {
          balance -= amount
          println(s"Get $amount")
        } else {
          println("Not so much money")
        }
      } else {
        self ! Open
        self ! Get(amount)
      }
  }

}

object Wallet {

  sealed trait WalletMsg

  case object Open extends WalletMsg

  case object Close extends WalletMsg

  case object Total extends WalletMsg

  case class Save(amount: Double) extends WalletMsg

  case class Get(amount: Double) extends WalletMsg

}




object ActorRun extends App {
  val system = ActorSystem("wallet", ConfigFactory.load)
  val walletActor = system.actorOf(Props(new Wallet).withDispatcher("akka.prio-dispatcher"), "walletActorName")

  import akka.actor.Wallet._

  walletActor ! Open
  walletActor ! Save(100)
  walletActor ! Total
  walletActor ! Get(80)
  walletActor ! Get(40)
  walletActor ! Close

  Thread.sleep(1000)
  system.terminate()
}