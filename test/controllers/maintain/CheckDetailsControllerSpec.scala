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
import connectors.{TrustsConnector, TrustsStoreConnector}
import mappers.{NonMigratingTrustDetails, TrustDetailsMapper}
import org.mockito.Matchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import play.api.inject.bind
import play.api.libs.json.{JsError, JsSuccess}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.http.HttpResponse
import utils.print.TrustDetailsPrintHelper
import viewmodels.AnswerSection
import views.html.maintain.CheckDetailsView

import scala.concurrent.Future

class CheckDetailsControllerSpec extends SpecBase with BeforeAndAfterEach {

  private lazy val checkDetailsRoute = controllers.maintain.routes.CheckDetailsController.onPageLoad().url
  private lazy val submitDetailsRoute = controllers.maintain.routes.CheckDetailsController.onSubmit().url
  private lazy val onwardRoute = frontendAppConfig.maintainATrustOverviewUrl

  private val mockTrustsStoreConnector: TrustsStoreConnector = mock[TrustsStoreConnector]

  override def beforeEach(): Unit = {
    reset(mockTrustsStoreConnector)
    when(mockTrustsStoreConnector.setTaskComplete(any())(any(), any()))
      .thenReturn(Future.successful(HttpResponse(OK, "")))
  }

  "CheckDetails Controller" when {

    ".onPageLoad" must {

      "return OK and the correct view" in {

        val mockPrintHelper = mock[TrustDetailsPrintHelper]

        val fakeAnswerSection = AnswerSection()

        when(mockPrintHelper(any())(any())).thenReturn(fakeAnswerSection)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[TrustDetailsPrintHelper].toInstance(mockPrintHelper))
          .build()

        val request = FakeRequest(GET, checkDetailsRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckDetailsView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(fakeAnswerSection)(request, messages).toString
      }
    }

    ".onSubmit" must {

      "set transforms and redirect" when {

        "not migrating" in {

          val userAnswers = emptyUserAnswers

          val mockTrustConnector = mock[TrustsConnector]
          val mockMapper = mock[TrustDetailsMapper]

          val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
            .overrides(
              bind[TrustsConnector].toInstance(mockTrustConnector),
              bind[TrustDetailsMapper].toInstance(mockMapper),
              bind[TrustsStoreConnector].toInstance(mockTrustsStoreConnector)
            ).build()

          when(mockTrustConnector.setNonMigratingTrustDetails(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

          val trustDetails = NonMigratingTrustDetails(trustUKProperty = true, trustRecorded = true, trustUKRelation = None, trustUKResident = true)

          when(mockMapper(any())).thenReturn(JsSuccess(trustDetails))

          val request = FakeRequest(POST, submitDetailsRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute

          verify(mockTrustConnector).setNonMigratingTrustDetails(eqTo(userAnswers.identifier), eqTo(trustDetails))(any(), any())

          application.stop()
        }

        "migrating" ignore {

        }
      }

      "return internal server error" when {

        "error mapping answers" in {

          val mockTrustConnector = mock[TrustsConnector]
          val mockMapper = mock[TrustDetailsMapper]

          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), affinityGroup = Agent)
            .overrides(
              bind[TrustsConnector].toInstance(mockTrustConnector),
              bind[TrustDetailsMapper].toInstance(mockMapper)
            ).build()

          when(mockMapper(any())).thenReturn(JsError())

          val request = FakeRequest(POST, submitDetailsRoute)

          val result = route(application, request).value

          status(result) mustEqual INTERNAL_SERVER_ERROR

          application.stop()
        }

        "error setting transforms" when {

          "not migrating" in {

            val mockMapper = mock[TrustDetailsMapper]
            val mockTrustConnector = mock[TrustsConnector]

            val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), affinityGroup = Agent)
              .overrides(
                bind[TrustsConnector].toInstance(mockTrustConnector),
                bind[TrustDetailsMapper].toInstance(mockMapper)
              ).build()

            val trustDetails = NonMigratingTrustDetails(trustUKProperty = true, trustRecorded = true, trustUKRelation = None, trustUKResident = true)

            when(mockMapper(any())).thenReturn(JsSuccess(trustDetails))

            when(mockTrustConnector.setNonMigratingTrustDetails(any(), any())(any(), any())).thenReturn(Future.failed(new Throwable("")))

            val request = FakeRequest(POST, submitDetailsRoute)

            val result = route(application, request).value

            status(result) mustEqual INTERNAL_SERVER_ERROR

            application.stop()
          }

          "migrating" ignore {

          }
        }
      }
    }

  }
}
