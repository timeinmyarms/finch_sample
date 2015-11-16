package com.ok.view
import io.finch._
import io.finch.request._
import io.finch.response._

/**
 * Created by olga.krekhovetska on 03.11.2015.
 */
object Test extends App{
  println("Hello")

  def hello(name: String) = new Service[HttpRequest, HttpResponse] = {
    def apply(req: HttpRequest) = for {
      title <- OptionalParam("title")(req)
    } yield Ok(Json.obj("greetings" -> s"Hello, ${title.getOrElse("")} $name!"))
  }

  val endpoint = new Endpoint[HttpRequest, HttpResponse] {
    def route = {
      // routes requests like '/hello/Bob?title=Mr.'
      case Method.Get -> Root / "hello" / name => hello(name)
    }
  }

}
