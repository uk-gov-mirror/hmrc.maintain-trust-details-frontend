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

package controllers.maintain

import base.SpecBase
import forms.YesNoFormProvider
import navigation.Navigator
import org.mockito.MockitoSugar
import pages.maintain.OwnsUkLandOrPropertyPage
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.maintain.OwnsUkLandOrPropertyView

class OwnsUkLandOrPropertyControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new YesNoFormProvider()
  val form: Form[Boolean] = formProvider.withPrefix("ownsUkLandOrPropertyYesNo")

  lazy val trustOwnUKLandOrPropertyControllerRoute: String = routes.OwnsUkLandOrPropertyController.onPageLoad().url

  "trustOwnUKLandOrProperty Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, trustOwnUKLandOrPropertyControllerRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[OwnsUkLandOrPropertyView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(OwnsUkLandOrPropertyPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trustOwnUKLandOrPropertyControllerRoute)

      val view = application.injector.instanceOf[OwnsUkLandOrPropertyView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true))(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[Navigator].toInstance(fakeNavigator))
          .build()

      val request = FakeRequest(POST, trustOwnUKLandOrPropertyControllerRoute)
        .withFormUrlEncodedBody(("value", "false"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }


    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, trustOwnUKLandOrPropertyControllerRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[OwnsUkLandOrPropertyView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trustOwnUKLandOrPropertyControllerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, trustOwnUKLandOrPropertyControllerRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}
