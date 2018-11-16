package ru.tinkoff.fintech.lesson09.examples.future

import scala.concurrent._
import scala.concurrent.duration._
import scala.io.StdIn

object Exmpl05ErrorHandling extends App {
  // global fork-joined bases EC
  implicit val ec: ExecutionContext = ExecutionContext.global

  def fWithBlocking(x: Int) = Future {
    println("computing " + x)
    blocking {
      StdIn.readLine()
    }
    if (scala.math.random() > 0.5) 42 / x
    else throw new IllegalStateException("bad state on " + x)
  }

  // recover (non fatal)
  val f01 = fWithBlocking(0).map(x => s"$x").recover {
    case x: IllegalStateException => "illegal state"
    case x: ArithmeticException => "illegal division"
  }

  println(Await.result(f01, 10 seconds))

  // recover with future
  val f02 = fWithBlocking(0).map(x => s"$x").recoverWith {
    case x: IllegalStateException => Future.successful("illegal state")
    case x: ArithmeticException => Future.successful("illegal division")
  }

  println(Await.result(f02, 10 seconds))

  // fallback
  val f03 = fWithBlocking(0).map(x => s"$x").fallbackTo(Future.successful("badaboom"))
  println(Await.result(f03, 10 seconds))
}