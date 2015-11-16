package com.ok.model

import io.circe.{Encoder, Json}
import io.circe.Json._

/**
 * Created by olga.krekhovetska on 04.11.2015.
 */
case class User(
  id: Long,
  firstName: String,
  lastName: String,
  yearsOld: Option[Int],
  version: Int
  )

object UserHelper{

  private def userSeq(u: User): Seq[(String, Json)] = Seq(
    "id" -> long(u.id),
    "firstName" -> string(u.firstName),
    "lastName" -> string(u.lastName))

  //convert book into book json
  implicit val userEncoder: Encoder[User] = Encoder.instance( b => Json.fromFields(userSeq(b)))
}
