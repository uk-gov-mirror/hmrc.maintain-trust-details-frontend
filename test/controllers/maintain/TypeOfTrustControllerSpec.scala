/*
 * Copyright 2021 HM Revenue & Customs
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
import connectors.TrustsConnector
import forms.EnumFormProvider
import generators.ModelGenerators
import models.TypeOfTrust
import models.TypeOfTrust.EmploymentRelated
import navigation.Navigator
import org.mockito.Matchers.any
import org.mockito.Mockito.{never, reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.maintain.TypeOfTrustPage
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse
import views.html.maintain.TypeOfTrustView

import scala.concurrent.Future

class TypeOfTrustControllerSpec extends SpecBase with BeforeAndAfterEach with ScalaCheckPropertyChecks with ModelGenerators {

  val form: Form[TypeOfTrust] = new EnumFormProvider()("typeOfTrust")

  lazy val typeOfTrustRoute: String = routes.TypeOfTrustController.onPageLoad().url

  val mockConnector: TrustsConnector = mock[TrustsConnector]

  override def beforeEach(): Unit = {
    reset(mockConnector)
    when(mockConnector.removeTrustTypeDependentTransforms(any())(any(), any()))
      .thenReturn(Future.successful(HttpResponse(OK, "")))
  }

  "TypeOfTrustController" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, typeOfTrustRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TypeOfTrustView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(TypeOfTrustPage, TypeOfTrust.InterVivosSettlement).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, typeOfTrustRoute)

      val view = application.injector.instanceOf[TypeOfTrustView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(TypeOfTrust.InterVivosSettlement))(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[Navigator].toInstance(fakeNavigator))
        .overrides(bind[TrustsConnector].toInstance(mockConnector))
        .build()

      val request = FakeRequest(POST, typeOfTrustRoute)
        .withFormUrlEncodedBody(("value", TypeOfTrust.EmploymentRelated.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "remove trust type dependent transforms" when {

      "previous answer EmploymentRelated and answer changed" in {

        forAll(arbitrary[TypeOfTrust].suchThat(_ != EmploymentRelated)) {
          newAnswer =>
            beforeEach()

            val userAnswers = emptyUserAnswers.set(TypeOfTrustPage, EmploymentRelated).success.value

            val application = applicationBuilder(userAnswers = Some(userAnswers))
              .overrides(bind[Navigator].toInstance(fakeNavigator))
              .overrides(bind[TrustsConnector].toInstance(mockConnector))
              .build()

            val request = FakeRequest(POST, typeOfTrustRoute)
              .withFormUrlEncodedBody(("value", newAnswer.toString))

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

            verify(mockConnector).removeTrustTypeDependentTransforms(any())(any(), any())

            application.stop()
        }
      }

      "new answer EmploymentRelated and answer changed" in {

        forAll(arbitrary[Option[TypeOfTrust]].suchThat(!_.contains(EmploymentRelated))) {
          previousAnswer =>
            beforeEach()

            val userAnswers = emptyUserAnswers.set(TypeOfTrustPage, previousAnswer).success.value

            val application = applicationBuilder(userAnswers = Some(userAnswers))
              .overrides(bind[Navigator].toInstance(fakeNavigator))
              .overrides(bind[TrustsConnector].toInstance(mockConnector))
              .build()

            val request = FakeRequest(POST, typeOfTrustRoute)
              .withFormUrlEncodedBody(("value", EmploymentRelated.toString))

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

            verify(mockConnector).removeTrustTypeDependentTransforms(any())(any(), any())

            application.stop()
        }
      }
    }

    "not remove trust type dependent transforms" when {

      "answer hasn't changed" in {

        forAll(arbitrary[(TypeOfTrust, TypeOfTrust)].suchThat(x => x._1 == x._2)) {
          tuple =>
            beforeEach()

            val userAnswers = emptyUserAnswers.set(TypeOfTrustPage, tuple._1).success.value

            val application = applicationBuilder(userAnswers = Some(userAnswers))
              .overrides(bind[Navigator].toInstance(fakeNavigator))
              .overrides(bind[TrustsConnector].toInstance(mockConnector))
              .build()

            val request = FakeRequest(POST, typeOfTrustRoute)
              .withFormUrlEncodedBody(("value", tuple._2.toString))

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

            verify(mockConnector, never()).removeTrustTypeDependentTransforms(any())(any(), any())

            application.stop()
        }
      }

      "neither previous answer nor new answer are EmploymentRelated" in {

        forAll(arbitrary[(Option[TypeOfTrust], TypeOfTrust)].suchThat(x => !x._1.contains(EmploymentRelated) && x._2 != EmploymentRelated)) {
          tuple =>
            beforeEach()

            val userAnswers = emptyUserAnswers.set(TypeOfTrustPage, tuple._1).success.value

            val application = applicationBuilder(userAnswers = Some(userAnswers))
              .overrides(bind[Navigator].toInstance(fakeNavigator))
              .overrides(bind[TrustsConnector].toInstance(mockConnector))
              .build()

            val request = FakeRequest(POST, typeOfTrustRoute)
              .withFormUrlEncodedBody(("value", tuple._2.toString))

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

            verify(mockConnector, never()).removeTrustTypeDependentTransforms(any())(any(), any())

            application.stop()
        }
      }
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(POST, typeOfTrustRoute)
        .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[TypeOfTrustView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, typeOfTrustRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, typeOfTrustRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
