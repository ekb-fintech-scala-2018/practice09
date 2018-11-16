package ru.tinkoff.fintech.lesson09.examples.future
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object Exmpl01CreateFuture extends App {
  implicit val ec = ExecutionContext.global

  // typical
  val f1 = Future {
    println("i am computing...")
  }

  // explicit execution context
  val f1Eplicit = Future {
    println("i am computing on explicit EC...")
  }(ExecutionContext.global)

  // explicit result
  val fSucc = Future.successful(42)
  val fFail = Future.failed(new RuntimeException("errrr"))
  val fUnknown = Future.fromTry(Try {1 / 0})
}