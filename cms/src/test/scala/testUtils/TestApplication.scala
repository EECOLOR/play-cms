package testUtils

import org.qirx.cms.Cms
import play.api.test.FakeApplication
import play.api.GlobalSettings
import play.api.mvc.Handler
import play.api.mvc.RequestHeader
import play.api.Mode

object TestApplication {

  def fakeApplication(global: Option[GlobalSettings] = None) =
    new FakeApplication(withGlobal = global)

  def apply(cms: Cms) = {
    val global =
      new GlobalSettings {
        override def onRouteRequest(request: RequestHeader): Option[Handler] =
          cms.handle(request, orElse = super.onRouteRequest)
      }

    fakeApplication(Some(global))
  }
}