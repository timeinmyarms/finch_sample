package com.ok.views.utils

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import io.finch.request.{NotParsed, NotValid, RequestError}
import io.finch.response.BadRequest

/**
 * Created by olga.krekhovetska on 05.11.2015.
 */
object Filters {

  val handleRequestValidationErrors: SimpleFilter[Request, Response] = new SimpleFilter[Request, Response] {
    def apply(req: Request, service: Service[Request, Response]): Future[Response] = {
      service(req).handle {
        case NotValid(item, rule) => BadRequest(rule)
        case NotParsed(item, _, cause) => BadRequest("Cannot parse request.")
        case error: RequestError => BadRequest(s"General error" + error.message)
        case a@_ => println(s"Unknown error: $a"); BadRequest(s"Unknown error: $a")
      }

    }
  }
}
