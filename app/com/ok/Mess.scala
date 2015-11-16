package com.ok

import com.ok.service.UserServiceHelper.CreateUserRequest
import com.ok.service.core.ServiceErrorReponses.ServiceErrorResponse
import io.finch.request._
import slick.dbio.DBIO

import scalaz.{EitherT, Functor, Monad, \/}

/**
 * Created by olga.krekhovetska on 05.11.2015.
 */
object Mess {
  import scala.concurrent.ExecutionContext.Implicits.global

  //do not work
  /*
      case class Address(street: String, city: String, postCode: String)

  val address: RequestReader[Address] = (
    param("street") ::
      param("city") ::
      param("postCode").shouldNot(beLongerThan(5))
    ).as[Address]

  case class User1(name: String, address: Address)

  val user: RequestReader[User1] =
    (param("name") :: address).as[User1]
    val user1: RequestReader[User1] = (
      (param("name") :: address).map {
        case name :: address :: HNil  => User1(name, address)
      }
      )*/

  /*  lazy val createUserReqParams: RequestReader[CreateUserRequest] = for {
      fName <- param("firstName").as[String]
      lName <- param("lastName").as[String]
      age <- paramOption("age").as[Int]
    } yield CreateUserRequest(fName, lName, age)*/

  lazy val createUserReqParams1: RequestReader[CreateUserRequest] = (
    param("firstName").as[String].should(beLongerThan(1)) ::
      param("lastName").as[String] ::
      paramOption("age").as[Int]
    ).as[CreateUserRequest]

  type EitherDBIO[A] = EitherT[DBIO, ServiceErrorResponse, A]

  def toEitherT[A](a: DBIO[ServiceErrorResponse \/ A]): EitherDBIO[A] = EitherT[DBIO, ServiceErrorResponse, A](a)

  implicit val DBIOFunctor = new Functor[DBIO] {
    def map[A, B](a: DBIO[A])(f: A => B): DBIO[B] = a map f
  }

  implicit val DBIOMonad = new Monad[DBIO] {
    def point[A](a: => A): DBIO[A] = DBIO.successful(a)
    def bind[A, B](fa: DBIO[A])(f: (A) => DBIO[B]): DBIO[B] = fa flatMap f
  }

  type Result[A] = ServiceErrorResponse \/ A

}
