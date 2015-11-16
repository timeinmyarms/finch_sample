package com.ok.persistence

import slick.jdbc.JdbcBackend.Database

/**
 * Created by olga.krekhovetska on 06.11.2015.
 */
object DBConfig {

  //TODO: use typesafe config method
  //val dbConfig =  DatabaseConfig.forConfig("posgre")

  //also user this executor = AsyncExecutor("test1", numThreads=10, queueSize=1000)
  val db = Database.forURL("jdbc:postgresql://localhost:5432/finchdb", user="finch", password="123", driver="org.postgresql.Driver")

}
