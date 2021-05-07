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
import mappers.TrustDetailsMapper
import models.TypeOfTrust.WillTrustOrIntestacyTrust
import models.{MigratingTrustDetails, NonMigratingTrustDetails, ResidentialStatusType, UkType, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import pages.maintain.AnswersCompletedPage
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

    reset(playbackRepository)
    when(playbackRepository.set(any())).thenReturn(Future.successful(true))
  }

  private val migratingTrustDetails = MigratingTrustDetails(
    lawCountry = None,
    administrationCountry = "GB",
    residentialStatus = ResidentialStatusType(uk = Some(UkType(scottishLaw = true, preOffShore = None))),
    trustUKRelation = None,
    trustUKResident = true,
    typeOfTrust = WillTrustOrIntestacyTrust,
    deedOfVariation = None,
    interVivos = None,
    efrbsStartDate = None
  )

  private val nonMigratingTrustDetails = NonMigratingTrustDetails(
    trustUKProperty = true,
    trustRecorded = true,
    trustUKRelation = None,
    trustUKResident = true
  )

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

          val trustDetails = nonMigratingTrustDetails

          when(mockMapper(any())).thenReturn(JsSuccess(trustDetails))

          val request = FakeRequest(POST, submitDetailsRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute

          verify(mockTrustConnector).setNonMigratingTrustDetails(eqTo(userAnswers.identifier), eqTo(trustDetails))(any(), any())

          val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(playbackRepository).set(uaCaptor.capture)
          uaCaptor.getValue.get(AnswersCompletedPage).get mustBe true

          application.stop()
        }

        "migrating" in {

          val userAnswers = emptyUserAnswers

          val mockTrustConnector = mock[TrustsConnector]
          val mockMapper = mock[TrustDetailsMapper]

          val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
            .overrides(
              bind[TrustsConnector].toInstance(mockTrustConnector),
              bind[TrustDetailsMapper].toInstance(mockMapper),
              bind[TrustsStoreConnector].toInstance(mockTrustsStoreConnector)
            ).build()

          when(mockTrustConnector.setMigratingTrustDetails(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

          val trustDetails = migratingTrustDetails

          when(mockMapper(any())).thenReturn(JsSuccess(trustDetails))

          val request = FakeRequest(POST, submitDetailsRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute

          verify(mockTrustConnector).setMigratingTrustDetails(eqTo(userAnswers.identifier), eqTo(trustDetails))(any(), any())

          val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(playbackRepository).set(uaCaptor.capture)
          uaCaptor.getValue.get(AnswersCompletedPage).get mustBe true

          application.stop()
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

            when(mockMapper(any())).thenReturn(JsSuccess(nonMigratingTrustDetails))

            when(mockTrustConnector.setNonMigratingTrustDetails(any(), any())(any(), any())).thenReturn(Future.failed(new Throwable("")))

            val request = FakeRequest(POST, submitDetailsRoute)

            val result = route(application, request).value

            status(result) mustEqual INTERNAL_SERVER_ERROR

            application.stop()
          }

          "migrating" in {

            val mockMapper = mock[TrustDetailsMapper]
            val mockTrustConnector = mock[TrustsConnector]

            val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), affinityGroup = Agent)
              .overrides(
                bind[TrustsConnector].toInstance(mockTrustConnector),
                bind[TrustDetailsMapper].toInstance(mockMapper)
              ).build()

            when(mockMapper(any())).thenReturn(JsSuccess(migratingTrustDetails))

            when(mockTrustConnector.setMigratingTrustDetails(any(), any())(any(), any())).thenReturn(Future.failed(new Throwable("")))

            val request = FakeRequest(POST, submitDetailsRoute)

            val result = route(application, request).value

            status(result) mustEqual INTERNAL_SERVER_ERROR

            application.stop()
          }
        }
      }
    }

  }
}
