package ru.tinkoff.fintech.lesson09.examples.actor
import akka.actor.{Actor, ActorSystem, Props}
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._

object Exmpl03TellAsk extends App {
  final case class Request(num: Double)
  final case class Result(x: Double)

  class Multiplier(rounds: Int) extends Actor {
    def receive = {
      case Request(num) =>
        sender() ! (num * rounds)
//        sender() ! Result(num * rounds) // this will fck up everything
    }
  }

  import akka.pattern.{ ask, pipe }
  import system.dispatcher

  val system = ActorSystem("myactorz")
  val multi = system.actorOf(Props(classOf[Multiplier], 10), "multi")

  implicit val timeout = Timeout(5 seconds)

  val f: Future[Double] =
    for {
      x <- ask(multi, Request(1)).mapTo[Double] // call pattern directly
      y <- (multi ask Request(x)).mapTo[Double] // call by implicit conversion
      z <- (multi ? Request(y)).mapTo[Double] // call by symbolic name
    } yield z

  f.foreach(println)
}
