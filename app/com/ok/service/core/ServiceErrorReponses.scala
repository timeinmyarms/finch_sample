package com.ok.service.core

/**
 * Created by olga.krekhovetska on 09.11.2015.
 */
object ServiceErrorReponses {
  
  sealed trait ServiceErrorResponse
  case class OptimisticLock(msg: String) extends ServiceErrorResponse
  case class EntityNotFound(msg: String) extends ServiceErrorResponse
}
