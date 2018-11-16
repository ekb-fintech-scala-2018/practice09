package ru.tinkoff.fintech.lesson09.examples.future

import scala.concurrent._
import scala.io.StdIn
import scala.util.Success
import scala.concurrent.duration._

object Exmpl07Promise extends App {
  val promise01 = Promise[String]
  val future01 = promise01.future

  new Thread(new Runnable {
    override def run(): Unit =
      promise01.complete(
        Success(
          StdIn.readLine()
        )
      )
  }).start()

  println(Await.result(future01 , 10 seconds))



  val promise02 = Promise[String]
  Future {
    blocking {
      promise02.complete(
        Success(
          StdIn.readLine()
        )
      )
    }
  }(ExecutionContext.global)

  val future02 = promise02.future
  println(Await.result(future02 , 10 seconds))
}