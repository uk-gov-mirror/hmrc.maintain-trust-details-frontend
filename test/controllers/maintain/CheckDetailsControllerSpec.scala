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
import connectors.TrustConnector
import mappers.{MappedTrustDetails, TrustDetailsMapper}
import org.mockito.Matchers.{any, eq => eqTo}
import org.mockito.Mockito.{never, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import pages.{BusinessRelationshipYesNoPage, TrustEEAYesNoPage, TrustOwnUKLandOrPropertyPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.http.HttpResponse
import utils.print.TrustDetailsPrintHelper
import viewmodels.AnswerSection
import views.html.maintain.CheckDetailsView

import scala.concurrent.Future

class CheckDetailsControllerSpec extends SpecBase with MockitoSugar with ScalaFutures {

  private lazy val checkDetailsRoute = controllers.maintain.routes.CheckDetailsController.onPageLoad().url
  private lazy val submitDetailsRoute = controllers.maintain.routes.CheckDetailsController.onSubmit().url
  private lazy val onwardRoute = frontendAppConfig.maintainATrustOverviewUrl

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

        "uk business relationship defined" in {

          val userAnswers = emptyUserAnswers

          val mockTrustConnector = mock[TrustConnector]
          val mockMapper = mock[TrustDetailsMapper]

          val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
            .overrides(
              bind[TrustConnector].toInstance(mockTrustConnector),
              bind[TrustDetailsMapper].toInstance(mockMapper)
            ).build()

          when(mockTrustConnector.setUkProperty(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
          when(mockTrustConnector.setTrustRecorded(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
          when(mockTrustConnector.setUkRelation(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

          when(mockMapper(any())).thenReturn(Some(MappedTrustDetails(trustUKProperty = true, trustRecorded = true, trustUKRelation = Some(true))))

          val request = FakeRequest(POST, submitDetailsRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute

          verify(mockTrustConnector).setUkProperty(eqTo(userAnswers.identifier), eqTo(true))(any(), any())
          verify(mockTrustConnector).setTrustRecorded(eqTo(userAnswers.identifier), eqTo(true))(any(), any())
          verify(mockTrustConnector).setUkRelation(eqTo(userAnswers.identifier), eqTo(true))(any(), any())

          application.stop()
        }

        "uk business relationship not defined" in {

          val userAnswers = emptyUserAnswers

          val mockTrustConnector = mock[TrustConnector]
          val mockMapper = mock[TrustDetailsMapper]

          val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
            .overrides(
              bind[TrustConnector].toInstance(mockTrustConnector),
              bind[TrustDetailsMapper].toInstance(mockMapper)
            ).build()

          when(mockTrustConnector.setUkProperty(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
          when(mockTrustConnector.setTrustRecorded(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

          when(mockMapper(any())).thenReturn(Some(MappedTrustDetails(trustUKProperty = true, trustRecorded = true, trustUKRelation = None)))

          val request = FakeRequest(POST, submitDetailsRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute

          verify(mockTrustConnector).setUkProperty(eqTo(userAnswers.identifier), eqTo(true))(any(), any())
          verify(mockTrustConnector).setTrustRecorded(eqTo(userAnswers.identifier), eqTo(true))(any(), any())
          verify(mockTrustConnector, never()).setUkRelation(any(), any())(any(), any())

          application.stop()
        }
      }

      "return internal server error" when {

        "error mapping answers" in {

          val userAnswers = emptyUserAnswers

          val mockTrustConnector = mock[TrustConnector]
          val mockMapper = mock[TrustDetailsMapper]

          val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
            .overrides(
              bind[TrustConnector].toInstance(mockTrustConnector),
              bind[TrustDetailsMapper].toInstance(mockMapper)
            ).build()

          when(mockTrustConnector.setUkProperty(any(), any())(any(), any())).thenReturn(Future.failed(new Throwable("")))
          when(mockTrustConnector.setTrustRecorded(any(), any())(any(), any())).thenReturn(Future.failed(new Throwable("")))
          when(mockTrustConnector.setUkRelation(any(), any())(any(), any())).thenReturn(Future.failed(new Throwable("")))

          when(mockMapper(any())).thenReturn(None)

          val request = FakeRequest(POST, submitDetailsRoute)

          val result = route(application, request).value

          status(result) mustEqual INTERNAL_SERVER_ERROR

          application.stop()
        }

        "error setting transforms" in {

          val userAnswers = emptyUserAnswers
            .set(TrustOwnUKLandOrPropertyPage, true).success.value
            .set(TrustEEAYesNoPage, true).success.value
            .set(BusinessRelationshipYesNoPage, true).success.value

          val mockTrustConnector = mock[TrustConnector]

          val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
            .overrides(bind[TrustConnector].toInstance(mockTrustConnector))
            .build()

          when(mockTrustConnector.setUkProperty(any(), any())(any(), any())).thenReturn(Future.failed(new Throwable("")))

          val request = FakeRequest(POST, submitDetailsRoute)

          val result = route(application, request).value

          status(result) mustEqual INTERNAL_SERVER_ERROR

          application.stop()
        }
      }
    }

  }
}
