package com.ok.bind

import com.ok.services.impl.{BookServiceImpl, UserServiceImpl}
import com.ok.services.{BookService, UserService}

/**
 * Created by olga.krekhovetska on 17.11.2015.
 */
object ServiceModule {

  lazy val userService: UserService = new UserServiceImpl
  lazy val bookService: BookService = new BookServiceImpl

}
