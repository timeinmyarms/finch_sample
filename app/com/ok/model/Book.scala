package com.ok.model

import com.ok.services.BookServiceHelper.CreateBookRequest
import io.circe.Json._
import io.circe.{Decoder, Encoder, Json}

/**
 * Created by olga.krekhovetska on 04.11.2015.
 */
case class Book(
  id: Long,
  name: String,
  author: String,
  ownerId: Option[Long],
  version: Int)


object BookHelper{

  def toBook(req: CreateBookRequest): Book = {
    Book(0, req.name, req.author, None, 0)
  }

  private def seq(b: Book): Seq[(String, Json)] = Seq(
    "id" -> long(b.id),
    "name" -> string(b.name))

  //convert book into book json
  implicit val bookEncoder: Encoder[Book] = Encoder.instance( b => Json.fromFields(seq(b)))
}