package ru.tinkoff.fintech.lesson09.examples.future

import scala.concurrent._
import scala.concurrent.duration._
import scala.io.StdIn

object Exmpl04Combinators extends App {
  // global fork-joined bases EC
  implicit val ec: ExecutionContext = ExecutionContext.global

  val ecExplicit: ExecutionContext = ExecutionContext.global

  def fWithBlocking1(x: Int) = Future {
    println("computing " + x)
    blocking {
      StdIn.readLine()
    }
    if (scala.math.random() > 0.1) x + 42
    else sys.error("bad luck on " + x)
  }

  def fWithBlocking2(x: Double) = Future {
    println("computing " + x)
    blocking {
      StdIn.readLine()
    }
    scala.math.random() + x
  }

  //ulgy maps & flatMaps with explicit EC
  println("exec ugly seq combinations")

  val f01 = fWithBlocking2(1).map(_ + 2)(ecExplicit)
  println(Await.result(f01, 10 seconds))

  val f02 = fWithBlocking2(2).flatMap{x => fWithBlocking2(x)}(ecExplicit)
  println(Await.result(f02, 10 seconds))

  //rewrite it more readable
  //fWithBlocking2 will not be started if fWithBlocking fails

  println("exec for seq combinations")

  val f03 = for {
    x1 <- fWithBlocking1(3)
    x2 <- fWithBlocking2(x1)
  } yield x1 + x2
  println(Await.result(f03, 10 seconds))


  //parallelize
  //fWithBlocking2 will be started unconditionally

  println("exec for parallel combinations")

  val f1 = fWithBlocking1(4)
  val f2 = fWithBlocking2(4)
  val f04 = for {
    x1 <- f1
    x2 <- f2
  } yield x1 + x2
  println(Await.result(f04, 10 seconds))


  //parallelize with zip
  //fWithBlocking2 will be started unconditionally

  println("exec for parallel with zip combinations")

  val f05 = for {
    (x1, x2) <- fWithBlocking1(4).zip(fWithBlocking2(4))
  } yield x1 + x2
  println(Await.result(f05, 10 seconds))

  // some moar

  Future{Future.successful(42)}.flatten
  Future{Future.successful(42)}.flatten.collect {
    case x: Int if x == 42 => x * 42
  } // will fail
}