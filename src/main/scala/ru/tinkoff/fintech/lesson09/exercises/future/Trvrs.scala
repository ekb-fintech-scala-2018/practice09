package ru.tinkoff.fintech.lesson09.exercises.future
import scala.concurrent.{ExecutionContext, Future}

object Trvrs {
  // возможно придется добавить неявные аргументы
  def foldF[T,A](fs: List[Future[T]], zero: A)(fold: (A, T) => A): Future[A] = ???
}
