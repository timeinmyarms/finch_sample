package com.ok.view.form

import com.ok.service.UserServiceHelper.CreateUserRequest
import io.finch.circe._
import io.finch.request._
import io.finch._
import io.circe.generic.auto._

/**
  * Created by olga.krekhovetska on 09.11.2015.
 */
object UserForm {

  lazy val createUserValidationRule = ValidationRule[CreateUserRequest]("Firstname should contains at least 1 symbol.") { req => req.firstName.length > 1 }
  lazy val updateUserValidationRule = ValidationRule[UpdateUserForm]("Firstname should contains at least 1 symbol.") { req => req.firstName.length > 1 }

  lazy val createUserForm =  body.as[CreateUserRequest].should(createUserValidationRule)
  lazy val updateUserForm = body.as[UpdateUserForm].should(updateUserValidationRule)
  lazy val deleteUserForm = body.as[Int]

  case class UpdateUserForm(
    firstName: String,
    lastName: String,
    age: Option[Int],
    version: Int
  )


}
