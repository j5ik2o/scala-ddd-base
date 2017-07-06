package com.github.j5ik2o.scala.ddd.functional

import cats.Monad
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

trait DBIOMonadInstance {

  val profile: JdbcProfile

  import profile.api._

  implicit def dbIOMonad(implicit ec: ExecutionContext) = new Monad[DBIO] {
    override def pure[A](x: A): DBIO[A] = DBIO.successful(x)

    override def flatMap[A, B](fa: DBIO[A])(f: (A) => DBIO[B]): DBIO[B] = fa.flatMap(f)

    override def tailRecM[A, B](a: A)(f: (A) => DBIO[Either[A, B]]): DBIO[B] = f(a).flatMap {
      case Left(a1) => tailRecM(a1)(f)
      case Right(b) => DBIO.successful(b)
    }

  }

}
