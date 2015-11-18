package com.ok.views.controllers

import com.ok.bind.UserServiceModule
import com.ok.model.{User, Book}
import com.ok.services.UserServiceHelper.{CreateUserRequest, UpdateUserRequest}
import com.ok.services.core.ServiceErrorReponses.ServiceErrorResponse
import com.ok.views.utils.ResultHelper
import com.twitter.util.{Future => TFuture}
import io.finch._
import org.slf4j.LoggerFactory

import scalaz._
/**
 * Created by olga.krekhovetska on 05.11.2015.
 */
object UserController extends ResultHelper with UserServiceModule {

  import com.ok.views.forms.UserForm._
  //NOTE: use import com.ok.model.UserHelper.userEncoder for specific json representation
  import userRouters._

  lazy val logger = LoggerFactory.getLogger(this.getClass)

  lazy val routers = createUser :+: getUser :+: updateUser :+: deleteUser :+: findAll

  def createUser : Endpoint[User]= createUserR { (req: CreateUserRequest) =>
    for {
      user <- userService.create(req)
    } yield {
      logger.info(s"User was created, userID=${user.id}")
      Created(user)
    }
  }

  def getUser: Endpoint[User] = getUserR { (id: Long) =>
    val result = for{
      user <- EitherT[TFuture, ServiceErrorResponse, User](userService.find(id))
    } yield {

      Ok(user)
    }
    processResult(result)
  }

  def updateUser: Endpoint[User] = updateUserR { (userId: Long, form: UpdateUserForm) =>
    val req = UpdateUserRequest(userId, form.firstName, form.lastName, form.age, form.version)
    val result = for{
      updatedUser <- EitherT[TFuture, ServiceErrorResponse, User](userService.update(req))
    } yield {
      logger.info(s"User was updated, userID=${updatedUser.id}")
      Ok(updatedUser)
    }
    processResult(result)
  }

  def deleteUser: Endpoint[Unit] = deleteUserR { (userId: Long, version: Int) =>
    val result = for {
      _ <- EitherT[TFuture, ServiceErrorResponse, Unit](userService.delete(userId, version))
    } yield {
      logger.info(s"User was deleted, userID=$userId")
      Ok
    }
    processResult(result)
  }

  def findAll: Endpoint[Seq[User]] = getAllUsersR { () =>
    Ok(userService.findAll())
  }

  object userRouters {
    lazy val userRoutersRoot = "users"
    lazy val createUserR = post(userRoutersRoot ? createUserForm )
    lazy val getUserR = get(userRoutersRoot / long("id"))
    lazy val updateUserR = put(userRoutersRoot / long("id") ? updateUserForm )
    lazy val deleteUserR = put(userRoutersRoot / long("id") ? deleteUserForm)
    lazy val getAllUsersR = get(userRoutersRoot)
  }
}
