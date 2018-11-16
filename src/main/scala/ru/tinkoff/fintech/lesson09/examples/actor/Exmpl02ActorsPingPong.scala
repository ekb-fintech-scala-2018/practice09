package ru.tinkoff.fintech.lesson09.examples.actor

import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}
import scala.concurrent.duration._

object Exmpl02ActorsPingPong extends App {
  //must be immutable
  case class Start(pongerCount: Int)
  case class Ping(round: Int)
  case object Pong

  class Pinger(rounds: Int) extends Actor {
    var count = 0

    //must not publish inner state via messages
    var pongersLeft = Set.empty[ActorRef]
    var pongers = List.empty[ActorRef]

    def receive = {
      case Start(count) =>
        pongers = (0 until count).map {x =>
          context.actorOf(Props(classOf[Ponger], s"ponger$x"), s"ponger$x")
        }.toList

        ping
      case Pong =>
        println(s"${self.path} received pong")
        pongersLeft = pongersLeft - sender()
        if (pongersLeft.isEmpty) {
          println(s"round ended: $count")
          if (count < rounds) {
            count += 1
            ping
          } else {
            println(s"kill all")
            pongers.foreach { p =>
              p ! PoisonPill
            }
            self ! PoisonPill
          }
        }
    }

    def ping: Unit = {
      pongersLeft = pongers.toSet
      pongers.foreach {p =>
        p ! Ping(count)
      }
    }
  }

  class Ponger(name: String) extends Actor {
    def receive = {
      case Ping(round) ⇒
        println(s"${self.path} received ping $round")
        sender() ! Pong
    }
  }

  val system = ActorSystem("myactorz")
  val pinger = system.actorOf(Props(classOf[Pinger], 10), "pinger")

  import system.dispatcher
  system.scheduler.scheduleOnce(500 millis) {
    pinger ! Start(3)
  }
}