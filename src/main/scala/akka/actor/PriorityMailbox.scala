package akka.actor

import akka.dispatch.{PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.Config

/**
  * Created by GongWenBo on 2018/1/30.
  */
class PriorityMailbox(setting: ActorSystem.Settings, config: Config) extends UnboundedPriorityMailbox(
  PriorityGenerator {
    case Wallet.Open => 0
    case Wallet.Close => 4
    case Wallet.Save(_) => 1
    case Wallet.Get(_) => 2
    case Wallet.Total => 4
    case PoisonPill => 4
    case _ => 4
  })
