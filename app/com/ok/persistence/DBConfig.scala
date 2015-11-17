package com.ok.persistence

import java.io.File

import com.typesafe.config.ConfigFactory
import slick.backend.DatabaseConfig
import slick.jdbc.JdbcBackend.Database

/**
 * Created by olga.krekhovetska on 06.11.2015.
 */
object DBConfig {

  val db =  Database.forConfig("db")

  //also user this executor = AsyncExecutor("test1", numThreads=10, queueSize=1000)
 // val db = Database.forURL("jdbc:postgresql://localhost:5432/finchdb", user="finch", password="123", driver="org.postgresql.Driver")

}
