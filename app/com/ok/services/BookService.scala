package com.ok.services

import com.ok.model.{User, Book}
import com.ok.services.BookServiceHelper.CreateBookRequest
import com.ok.services.core.ServiceErrorReponses
import ServiceErrorReponses.{ServiceErrorResponse, EntityNotFound, OptimisticLock}
import com.twitter.util.{Future => TFuture}

import scalaz.\/

/**
 * Created by olga.krekhovetska on 09.11.2015.
 */
trait BookService {

  def create(req : CreateBookRequest): TFuture[Book]

  def order(bookId: Long, ownerId: Long, version: Int): TFuture[ServiceErrorResponse \/ Book]

  def find(id: Long): TFuture[EntityNotFound \/ Book]

  def delete(bookId: Long, version: Int): TFuture[OptimisticLock \/ Unit]

  def findAll(userId: Option[Long]): TFuture[Seq[Book]]
}



object BookServiceHelper {

  case class CreateBookRequest(
    name: String,
    author: String)

  case class UpdateBookRequest(
    name: String,
    author: String,
    ownerId: Option[Long],
    version: Int)

  case class OrderBookRequest(
    ownerId: Long,
    version: Int
  )

}
