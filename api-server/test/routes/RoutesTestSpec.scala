package routes

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.FakeRequest
import play.api.test.Helpers._

/**
  * Functional tests start a Play application internally, available
  * as `app`.
  */
class RoutesTestSpec extends PlaySpec with GuiceOneAppPerSuite {

  "Routes" should {

    "send 404 on a bad request" in {
      route(app, FakeRequest(GET, "/boum")).map(status(_)) mustBe Some(NOT_FOUND)
    }

    "send 200 on '/' " in {
      route(app, FakeRequest(GET, "/")).map(status(_)) mustBe Some(OK)
    }

    /**
      * for YANOLJA
      */
    "send 200 on '/business/hip'" in {
      route(app, FakeRequest(GET, "/business/hip")).map(status(_)) mustBe Some(OK)
    }
    "send 200 on '/business/reviews'" in {
      route(app, FakeRequest(GET, "/business/reviews")).map(status(_)) mustBe Some(OK)
    }
    "send 200 on '/business/tips'" in {
      route(app, FakeRequest(GET, "/business/tips")).map(status(_)) mustBe Some(OK)
    }
    "send 200 on '/business/hip:name'" in {
      route(app, FakeRequest(GET, "/business/hip/Arepera")).map(status(_)) mustBe Some(OK)
    }
  }
}