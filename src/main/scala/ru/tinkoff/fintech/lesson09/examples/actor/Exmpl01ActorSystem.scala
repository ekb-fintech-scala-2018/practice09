package ru.tinkoff.fintech.lesson09.examples.actor

import akka.actor.ActorSystem

object Exmpl01ActorSystem extends App {
  val system = ActorSystem("myactorz")

  println("das actor system started")

  system.terminate()
}
