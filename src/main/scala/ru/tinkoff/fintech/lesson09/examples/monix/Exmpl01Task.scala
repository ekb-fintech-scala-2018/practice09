package ru.tinkoff.fintech.lesson09.examples.monix
import monix.eval.Task

object Exmpl01Task extends App {
  //we don't need any Scheduler for task creation
  val scheduler = monix.execution.Scheduler.Implicits.global

  val task01 = Task.now {
    println("Effect01")
    "task01"
  }

  println("\ntask01 created\n")

  val task02 = Task.eval {
    println("Effect02")
    "task02"
  }

  println("\ntask02 created\n")

  //we need scheduler (EC) for task chain running
  task02.runToFuture(scheduler).foreach(println)(scheduler)
  task02.runToFuture(scheduler).foreach(println)(scheduler)

  val task03 = Task.evalOnce {
    println("Effect03")
    "task03"
  }

  println("\ntask03 created\n")

  task03.runToFuture(scheduler).foreach(println)(scheduler)
  task03.runToFuture(scheduler).foreach(println)(scheduler)


  val task01FM = task02.flatMap(x => Task.eval {
    println("flatmap in da haus")
    x + " flatmap"
  })

  println("\nflatmapped\n")

  task01FM.runToFuture(scheduler).foreach(println)(scheduler)
  task01FM.runToFuture(scheduler).foreach(println)(scheduler)

  val task01M = task02.map{x =>
    println("flatmap in da haus")
    x + " map"
  }

  println("\nmapped\n")

  task01M.runToFuture(scheduler).foreach(println)(scheduler)
}
