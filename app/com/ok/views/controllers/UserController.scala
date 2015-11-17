package com.ok.views.controllers

import com.ok.model.User
import com.ok.services.UserServiceHelper.{CreateUserRequest, UpdateUserRequest}
import com.ok.services.core.ServiceErrorReponses.{EntityNotFound, OptimisticLock, ServiceErrorResponse}
import com.ok.services.impl.UserServiceImpl
import com.ok.views.utils.ResultHelper
import com.twitter.util.{Future => TFuture}
import io.finch._

import scalaz._
/**
 * Created by olga.krekhovetska on 05.11.2015.
 */
object UserController extends ResultHelper {

  import com.ok.views.forms.UserForm._
  //NOTE: use import com.ok.model.UserHelper.userEncoder for specific json representation
  import userRouters._

  object userRouters {
    lazy val userRoutersRoot = "users"
    lazy val createUserR = post(userRoutersRoot ? createUserForm )
    lazy val getUserR = get(userRoutersRoot / long("id"))
    lazy val updateUserR = put(userRoutersRoot / long("id") ? updateUserForm )
    lazy val deleteUserR = put(userRoutersRoot / long("id") ? deleteUserForm)
    lazy val getAllUsersR = get(userRoutersRoot)
  }

  lazy val userService = new UserServiceImpl

  lazy val routers = createUser :+: getUser :+: updateUser :+: deleteUser :+: findAll

  def createUser : Endpoint[User]= createUserR { (req: CreateUserRequest) =>
    for {
      user <- userService.create(req)
    } yield Created(user)
  }

  def getUser: Endpoint[User] = getUserR { (id: Long) =>
    val result = for{
      user <- EitherT[TFuture, ServiceErrorResponse, User](userService.find(id))
    } yield Ok(user)
    processResult(result)
  }

  def updateUser: Endpoint[User] = updateUserR { (userId: Long, form: UpdateUserForm) =>
    val req = UpdateUserRequest(userId, form.firstName, form.lastName, form.age, form.version)
    val result = for{
      updatedUser <- EitherT[TFuture, ServiceErrorResponse, User](userService.update(req))
    } yield Ok(updatedUser)
    processResult(result)
  }

  def deleteUser: Endpoint[Unit] = deleteUserR { (userId: Long, version: Int) =>
    val result = for {
      _ <- EitherT[TFuture, ServiceErrorResponse, Unit](userService.delete(userId, version))
    } yield Ok
    processResult(result)
  }

  def findAll: Endpoint[Seq[User]] = getAllUsersR { () =>
    Ok(userService.findAll())
  }
}
