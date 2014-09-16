package testUtils

import org.qirx.cms.Cms
import play.api.test.FakeApplication
import play.api.GlobalSettings
import play.api.mvc.Handler
import play.api.mvc.RequestHeader
import play.api.Mode

object TestApplication {

  def fakeApplication(global: Option[GlobalSettings] = None) = {
    println("creating new fake application")
    new FakeApplication(
      withGlobal = global,
      additionalConfiguration = Map(
        "logger.play" -> "WARN",
        "logger.application" -> "WARN",
        "logger.root" -> "ERROR",
        "logger.ch.qos.logback" -> "ERROR"
      ))
  }

  def apply(cms: Cms) = {
    val global =
      new GlobalSettings {
        override def onRouteRequest(request: RequestHeader): Option[Handler] =
          cms.handle(request, orElse = super.onRouteRequest)
      }

    fakeApplication(Some(global))
  }
}