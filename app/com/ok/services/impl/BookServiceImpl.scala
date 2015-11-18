package com.ok.services.impl

import com.ok.model.{Book, BookHelper}
import com.ok.persistence.{Tables, PersistenceErrors, DBConfig}
import DBConfig.db
import Tables.{books, users}
import PersistenceErrors._
import com.ok.services.BookService
import com.ok.services.BookServiceHelper.CreateBookRequest
import com.ok.services.core.ServiceErrorReponses
import ServiceErrorReponses.{EntityNotFound, OptimisticLock, ServiceErrorResponse}
import com.twitter.util.{Future => TFuture}
import org.slf4j.LoggerFactory
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future
import scalaz.\/
import scalaz.syntax.either._
import scalaz.syntax.std.boolean._
import scalaz.syntax.std.option._

/**
 * Created by olga.krekhovetska on 09.11.2015.
 */
class BookServiceImpl extends BookService {

  import com.ok.utils.TwitterConverters._
  import scala.concurrent.ExecutionContext.Implicits.global

  val logger = LoggerFactory.getLogger(this.getClass)

  override def create(req: CreateBookRequest): TFuture[Book] = {
    val bookToCreate = BookHelper.toBook(req)
    val query = books.insert(bookToCreate)
    db.run(query) map {
      insertedId => bookToCreate.copy(id = insertedId)
    }
  }

  override def order(bookId: Long, ownerId: Long, version: Int): TFuture[ServiceErrorResponse \/ Book] = {

    val actions: DBIO[Book] = (for{
      bookOpt <- books.findById(bookId)
      book <- if(bookOpt.isDefined) DBIO.successful(bookOpt.get) else DBIO.failed(new NotFoundExcp("Book is not found."))
      ownerOpt <- users.findById(ownerId)

      owner <- if(ownerOpt.isDefined) DBIO.successful(ownerOpt.get) else DBIO.failed(new NotFoundExcp("User is not found."))
      bookToUpdate = bookOpt.get.copy(ownerId = Some(ownerId), version = version + 1)

      updatedCount <- books.update(bookToUpdate, version)
      _ <- if(updatedCount == 1) DBIO.successful(updatedCount) else DBIO.failed(OptimisticLockExcp("Book was modified."))

    //  userUP <- users.filter(u => u.id === ownerId).update(ownerOpt.get.copy(version = version + 1))
    } yield bookToUpdate)

    val updateResult: Future[ServiceErrorResponse \/ Book] = db.run(actions.transactionally).map(_.right).recover {
      case OptimisticLockExcp(msg) =>
        logger.warn(s"Book update: optimistic lock, bookID=$bookId")
        OptimisticLock(msg).left
      case NotFoundExcp(msg) => EntityNotFound(msg).left
    }
    updateResult
  }

  override def findAll(userId: Option[Long]): TFuture[Seq[Book]] = {
    val query = userId.map{id => books.filter(_.owner === id)}.getOrElse(books)
    db.run(query.result)
  }

  override def delete(bookId: Long, version: Int): TFuture[\/[OptimisticLock, Unit]] = {
    for {
      deletedCount <- db.run(books.delete(bookId, version))
    } yield {
      (deletedCount == 1) either (()) or {
        logger.warn(s"Book deletion: optimistic lock, bookID=$bookId")
        OptimisticLock("Book was modified.")
      }
    }
  }

  override def find(id: Long): TFuture[EntityNotFound \/ Book]= {
    for {
      bookOpt <- db.run(books.findById(id))
    } yield {
      bookOpt \/> EntityNotFound("Book is not found.")
    }
  }

}
