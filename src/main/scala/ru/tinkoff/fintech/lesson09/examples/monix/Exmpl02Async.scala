package ru.tinkoff.fintech.lesson09.examples.monix

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import monix.execution.Scheduler

import scala.concurrent.Await
import scala.util.Try
import concurrent.duration._

object Exmpl02Async extends App {
  val source = Task(println(s"Running on thread: ${Thread.currentThread.getName}"))

  def defaultScheduler = {
    println("same scheduler")
    source.runToFuture.foreach(_ => ())
    source.executeAsync.runToFuture.foreach(_ => ())
  }

  def anotherScheduler = {
    println("other scheduler")
    lazy val io = Scheduler.io(name="my-io")

    val forked = source.executeOn(io)
    source.runToFuture.foreach(_ => ())
    forked.runToFuture.foreach(_ => ())
  }

  def jumpingScheduler = {
    println("jumping scheduler")
    lazy val io = Scheduler.io(name="my-io")

    val forked = source.executeOn(io)
    source.executeAsync // executes on default
      .flatMap(_ => forked) // executes on io
      .asyncBoundary
      .flatMap(_ => source)
      .runToFuture.foreach(_ => ())
  }

  //async API with cancellation
  def callback = {
    def evalDelayed[A](delay: FiniteDuration)(f: => A): Task[A] = {
      Task.create { (scheduler, callback) =>
        val cancelable = scheduler.scheduleOnce(delay) {
          callback(Try(f))
        }
        cancelable
      }
    }

    Task.race(
      evalDelayed(1 seconds) { println("one second") },
      evalDelayed(10 seconds) { println("ten seconds") }
    ).runToFuture

    Thread.sleep(20000)
  }

  def fiber = {
    val after = Task(println("after delay")).delayExecution(1 seconds)
    val task = Task(println("i am task")).flatMap(_ => after)

    val res = for {
      f <- task.start
      _ <- f.cancel
    } yield println("running")

    res.runToFuture

    Thread.sleep(5000)
  }

//  defaultScheduler
//  anotherScheduler
//  jumpingScheduler
//  callback
  fiber
}