package com.ok.service.impl

import com.ok.model.User
import com.ok.persistence.{Tables, PersistenceErrors, DBConfig}
import DBConfig.db
import Tables.users
import PersistenceErrors._
import com.ok.service.UserService
import com.ok.service.UserServiceHelper.{CreateUserRequest, UpdateUserRequest}
import com.ok.service.core.ServiceErrorReponses
import ServiceErrorReponses.{EntityNotFound, ServiceErrorResponse, OptimisticLock}
import com.twitter.util.{Future => TFuture}
import slick.driver.PostgresDriver.api._


import scala.concurrent.Future
import scalaz.\/
import scalaz.syntax.either._
import scalaz.syntax.std.boolean._
import scalaz.syntax.std.option._

/**
 * Created by olga.krekhovetska on 04.11.2015.
 */
class UserServiceImpl extends UserService {
  import scala.concurrent.ExecutionContext.Implicits.global
  //convert scala.concurrent.Future  to com.twitter.util.Future
  import com.ok.utils.TwitterConverters._

  override def create(req: CreateUserRequest): TFuture[User] = {
    val user = User(0, req.firstName, req.lastName, req.age, 0)

    val created = db.run(users.insert(user)).map { insertedId =>
      user.copy(id = insertedId)
    }
    created
  }

  override def update(req: UpdateUserRequest): TFuture[ServiceErrorResponse \/ User] = {
    val actions = for{
      userOpt <- users.findById(req.id)
      user <- userOpt match {
        case Some(u) => DBIO.successful(u)
        case None => DBIO.failed(NotFoundExcp("User is not found."))
      }
      userToUpdate = user.copy(firstName = req.firstName, lastName = req.lastName, yearsOld = req.age, version = req.version + 1)
      updatedCount <- users.update(userToUpdate, req.version)
      _ <- if(updatedCount == 1) DBIO.successful(updatedCount) else DBIO.failed(OptimisticLockExcp("User was modified."))
    } yield userToUpdate

    val updateResult: Future[ServiceErrorResponse \/ User] = db.run(actions.transactionally).map(_.right).recover {
      case OptimisticLockExcp(msg) => OptimisticLock(msg).left
      case NotFoundExcp(msg) => EntityNotFound(msg).left
    }
    updateResult
  }

  override def find(id: Long): TFuture[EntityNotFound \/ User] = {
    for {
      userOpt <- db.run(users.findById(id))
    } yield {
        userOpt \/> EntityNotFound("User is not found.")
      }
  }

  override def delete(id: Long, version: Int): TFuture[OptimisticLock \/ Unit] = {
   for{
      deletedCount <- db.run(users.delete(id, version))
    } yield{
      (deletedCount == 1) either (()) or OptimisticLock("User was modified.")
    }
  }

  override def findAll(): TFuture[Seq[User]] = db.run(users.findAll)
}
