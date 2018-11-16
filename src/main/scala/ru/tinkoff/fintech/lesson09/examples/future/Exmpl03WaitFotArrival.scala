package ru.tinkoff.fintech.lesson09.examples.future

import scala.io.StdIn
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Exmpl03WaitFotArrival extends App {
  // global fork-joined bases EC
  implicit val ec: ExecutionContext = ExecutionContext.global

  val fWithBlocking = Future {
    println("i am computing...")
    blocking {
      StdIn.readLine()
    }
    if (scala.math.random() > 0.5) 42
    else sys.error("bad luck")
  }

  fWithBlocking.onComplete {
    case Success(x) => println("success callback 01 " + x)
    case Failure(e) => println("failure callback 01 " + e)
  }

  val resF = fWithBlocking.andThen {
    case Success(x) => println("success callback 02 " + x)
    case Failure(e) => println("failure callback 02 " + e)
  }

  resF.foreach {x =>
    println("success callback 03 " + x)
  }

  println(Await.result(resF, 10 seconds))

/*
  while(!fWithBlocking.isCompleted) {}
  println(fWithBlocking.value)
*/
}