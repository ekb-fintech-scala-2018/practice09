package ru.tinkoff.fintech.lesson09.examples.future
import scala.concurrent.{ExecutionContext, Future}

object Exmpl06FutureOb extends App {
  implicit val ec: ExecutionContext = ExecutionContext.global

  // parallel !
  // List[A] + A => B == Future[List[B]]
  val x1: Future[List[String]] = Future.traverse(
    List(1, 2, 3)
  )(x => Future(s"$x$x$x"))

  // parallel !
  // List[Future[A]] => Future[List[A]]
  val x2: Future[List[Int]] = Future.sequence(
    List(Future(1), Future(2), Future(3))
  )
}