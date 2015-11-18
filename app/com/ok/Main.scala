package com.ok

import com.ok.persistence.DBSetUp
import com.ok.views.controllers.{BookController, UserController}
import com.ok.views.utils.Filters.handleRequestValidationErrors
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await
import org.slf4j.LoggerFactory

import scala.concurrent.duration._


/**
 * Created by olga.krekhovetska on 03.11.2015.
 */
object Main extends App {
  import scala.concurrent.ExecutionContext.Implicits.global
  import io.finch.circe._
  import io.finch._
  import io.circe.generic.auto._
  val logger = LoggerFactory.getLogger(this.getClass)

  logger.info("Creating DB schema...")
  scala.concurrent.Await.ready(

  //FIXME:: check if db is already exists!
    DBSetUp.createSchema.map{ _ =>
      logger.info("DB schema is created.")
  }.recover{
      case e: Throwable =>
        logger.error("=====================================================")
        logger.error(s"error: ${e.getMessage}: ${Option(e.getCause).map(_.getMessage).getOrElse("")}", e)
        logger.error("=====================================================")
    }
    , 120 seconds)

  lazy val apiRouters = UserController.routers :+: BookController.routers
  lazy val service = apiRouters.toService
  val api: Service[Request, Response] = handleRequestValidationErrors andThen service
  Await.ready(Http.serve(":8080", api))
}

