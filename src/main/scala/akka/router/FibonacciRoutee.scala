package akka.router

import akka.actor.{Actor, ActorSystem, Props}
import akka.routing.FromConfig

import scala.annotation.tailrec

/**
  * Created by GongWenBo on 2018/1/30.
  */
case class FibonacciNumber(num: Int)

class FibonacciRoutee extends Actor {
  override def receive: Receive = {
    case FibonacciNumber(num) =>
      val result = calculate(num)
      println(s"${self.path.name} ....Result is $result")
  }

  private def calculate(num: Int): Int = {
    @tailrec
    def fib(n: Int, b: Int, a: Int): Int = n match {
      case 0 => a
      case _ => fib(n - 1, a + b, b)
    }
    fib(num, 1, 0)
  }
}

object RouterRun extends App{
  val system = ActorSystem("routeSystem")
  val router = system.actorOf(FromConfig.props(Props(new FibonacciRoutee)),"balance-pool-router")

  router !  FibonacciNumber(5)
  router !  FibonacciNumber(15)
  router !  FibonacciNumber(10)
  router !  FibonacciNumber(8)

  scala.io.StdIn.readLine()
  system.terminate()
}
