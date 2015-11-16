package com.ok.service

import _root_.com.ok.model.User
import com.ok.service.core.ServiceErrorReponses
import ServiceErrorReponses.{ServiceErrorResponse, EntityNotFound, OptimisticLock}
import com.twitter.util.{Future => TFuture}


import scalaz.\/

/**
 * Created by olga.krekhovetska on 04.11.2015.
 */
trait UserService {
  import UserServiceHelper._

  def create(req: CreateUserRequest): TFuture[User]

  def update(req: UpdateUserRequest): TFuture[ServiceErrorResponse \/ User]

  def find(id: Long): TFuture[EntityNotFound \/ User]

  def delete(id: Long, version: Int): TFuture[OptimisticLock \/ Unit]

  def findAll(): TFuture[Seq[User]]
}

object UserServiceHelper{
  case class CreateUserRequest(
     firstName: String,
     lastName: String,
     age: Option[Int])

  case class UpdateUserRequest(
     id: Long,
     firstName: String,
     lastName: String,
     age: Option[Int],
     version: Int)
}
