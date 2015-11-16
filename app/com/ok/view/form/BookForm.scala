package com.ok.view.form

import com.ok.service.BookServiceHelper.{OrderBookRequest, CreateBookRequest, UpdateBookRequest}
import io.finch.circe._
import io.finch.request._
import io.finch._
import io.circe.generic.auto._


/**
 * Created by olga.krekhovetska on 16.11.2015.
 */
object BookForm {

  lazy val createBookForm = body.as[CreateBookRequest]
  lazy val updateBookForm = body.as[UpdateBookRequest]
  lazy val orderBookForm = body.as[OrderBookRequest]
  lazy val deleteBookForm = body.as[Int]

}
