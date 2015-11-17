package com.ok.views.controllers

import com.ok.model.Book
import com.ok.services.BookServiceHelper.{CreateBookRequest, OrderBookRequest}
import com.ok.services.core.ServiceErrorReponses.ServiceErrorResponse
import com.ok.services.impl.BookServiceImpl
import com.ok.views.utils.ResultHelper
import com.twitter.util.{Future => TFuture}
import io.finch.request._
import io.finch.{Endpoint, _}

import scalaz.EitherT


/**
 * Created by olga.krekhovetska on 05.11.2015.
 */
object BookController extends ResultHelper {

  import bookRouters._
  import com.ok.views.forms.BookForm._
  import io.finch.circe._

  lazy val routers = createBook :+: orderBook :+: getBook :+: deleteBook :+: getAll

  lazy val bookService = new BookServiceImpl

  def createBook: Endpoint[Book] = createBookR { (req: CreateBookRequest) =>
    for{
      createdBook <- bookService.create(req)
    } yield Created(createdBook)
  }

  def orderBook: Endpoint[Book] = orderBookR { (bookId: Long, req: OrderBookRequest) =>
    val result = for{
      updatedBook <- EitherT[TFuture, ServiceErrorResponse, Book](bookService.order(bookId, req.ownerId, req.version))
    } yield Ok(updatedBook)
    processResult(result)
  }

  def getBook: Endpoint[Book] = getBookR { (bookId: Long) =>
    val result = for {
      book <- EitherT[TFuture, ServiceErrorResponse, Book](bookService.find(bookId))
    } yield Ok(book)
    processResult(result)
  }

  def deleteBook: Endpoint[Unit] = deleteBookR {(bookId: Long, version: Int) =>
    val result = for {
      _ <- EitherT[TFuture, ServiceErrorResponse, Unit](bookService.delete(bookId, version))
    } yield Ok
    processResult(result)
  }

  def getAll: Endpoint[Seq[Book]] = getAllBookR {(userId: Option[Long]) =>
    println(s"userID: $userId")
      Ok(bookService.findAll(userId))
  }

  object bookRouters {
    lazy val bookRoutersRoot = "books"
    lazy val orderBookR = put(bookRoutersRoot / long("id") ? orderBookForm)
    lazy val createBookR = post(bookRoutersRoot ? createBookForm)
    //lazy val updateBookR = put(bookRoutersRoot / long("id") ? updateBookForm)
    lazy val getBookR = get(bookRoutersRoot / long("id"))
    lazy val getAllBookR = get(bookRoutersRoot ? paramOption("ownerId").as[Long])
    lazy val deleteBookR = put(bookRoutersRoot / long("id") ? deleteBookForm)
  }

}
