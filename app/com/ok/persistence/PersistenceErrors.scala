package com.ok.persistence

/**
 * Created by olga.krekhovetska on 06.11.2015.
 */
object PersistenceErrors {

  sealed trait PersistenceException extends java.lang.Exception{
    def msg : String
  }
  case class OptimisticLockExcp(msg: String) extends PersistenceException
  case class NotFoundExcp(msg: String) extends PersistenceException

}


