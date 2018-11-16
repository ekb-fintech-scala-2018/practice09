package ru.tinkoff.fintech.lesson09.examples.future

import java.util.concurrent.Executors

import scala.concurrent.{ExecutionContext, Future, blocking}
import scala.io.StdIn

object Exmpl02ExecutionContext extends App {
  // global fork-joined bases EC
  implicit val ec: ExecutionContext = ExecutionContext.global

  val fWithBlocking = Future {
    println("i am computing...")
    blocking {
      StdIn.readLine()
    }
  }


  // other way of EC construction

  val cpuBoundEC = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(4))
  val blockingIoBoundEC = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
}