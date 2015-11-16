package com.ok.persistence

import Tables.{books, users}
import DBConfig._
import slick.driver.PostgresDriver.api._
import slick.jdbc.meta.MTable

/**
 * Created by olga.krekhovetska on 06.11.2015.
 */
object DBSetUp {

  lazy val schema = users.schema ++ books.schema
  val tables = List(users, books)

  def createSchema = {
    db.run(DBIO.seq(schema.create))
  }

  def dropSchema = db.run(DBIO.seq(schema.drop))

}
