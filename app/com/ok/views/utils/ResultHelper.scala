package com.ok.views.utils

import com.ok.services.core.ServiceErrorReponses.{EntityNotFound, OptimisticLock, ServiceErrorResponse}
import com.twitter.util.{Future => TFuture}
import io.finch._

import scalaz.{EitherT, Functor, _}

/**
 * Created by olga.krekhovetska on 16.11.2015.
 */
trait ResultHelper {

  implicit val FutureFunctor = new Functor[TFuture] {
    def map[A, B](a: TFuture[A])(f: A => B): TFuture[B] = a map f
  }

  implicit val FutureMonad = new Monad[TFuture] {
    def point[A](a: => A): TFuture[A] = TFuture(a)
    def bind[A, B](fa: TFuture[A])(f: (A) => TFuture[B]): TFuture[B] = fa flatMap f
  }

  def processResult[A](result : EitherT[TFuture, ServiceErrorResponse, Output.Payload[A]]): TFuture[Output[A]] = {
    result.run.map(_.leftMap( err => handleErrors(err)))
      .map { _ match {
        case \/-(res) => res
        case -\/(err) => err
      }
    }
  }

  def handleErrors(error: ServiceErrorResponse): Output[Nothing] = {
   error match {
      case EntityNotFound(msg) => NotFound(("message", msg))
      case OptimisticLock(msg) => PreconditionFailed(("message", msg))
       //TODO: add some more results
    }
  }
}
