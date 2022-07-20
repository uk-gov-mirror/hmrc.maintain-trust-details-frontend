/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import base.SpecBase
import play.api.mvc.{AnyContent, MessagesControllerComponents, Request}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Configuration, Environment}
import config.AppConfig

class SessionTimeoutControllerSpec extends SpecBase {

  object TestSessionTimeoutController extends SessionTimeoutController(
    app.injector.instanceOf[AppConfig],
    app.injector.instanceOf[Configuration],
    app.injector.instanceOf[Environment],
    app.injector.instanceOf[MessagesControllerComponents]
  )

  "SessionTimeoutController" when {

    "keepAlive" should {
      "stay on current page with current session" in {
        val fakeRequest: Request[AnyContent] = FakeRequest().withSession()
        val res = TestSessionTimeoutController.keepAlive(fakeRequest)
        status(res) mustEqual OK
      }
    }

    "timeout" should {
      "redirect to session expired page with new session" in {
        val fakeRequest: Request[AnyContent] = FakeRequest().withSession()
        val res = TestSessionTimeoutController.timeout(fakeRequest)
        status(res) mustEqual SEE_OTHER
        redirectLocation(res).value mustEqual routes.SessionExpiredController.onPageLoad.url
      }
    }
  }
}
