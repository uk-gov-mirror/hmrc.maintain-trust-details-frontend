/*
 * Copyright 2023 HM Revenue & Customs
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
import generators.ModelGenerators
import mappers.TrustDetailsMapper
import models.TaskStatus.Completed
import models.TypeOfTrust.{EmploymentRelated, HeritageMaintenanceFund, WillTrustOrIntestacyTrust}
import models._
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.bind
import play.api.libs.json.{JsError, JsSuccess}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.http.HttpResponse
import utils.print.TrustDetailsPrintHelper
import viewmodels.AnswerSection
import views.html.maintain.CheckDetailsView

import java.time.LocalDate
import scala.concurrent.Future

class CheckDetailsControllerSpec extends SpecBase with BeforeAndAfterEach with ScalaCheckPropertyChecks with ModelGenerators {

  private lazy val checkDetailsRoute = controllers.maintain.routes.CheckDetailsController.onPageLoad().url
  private lazy val submitDetailsRoute = controllers.maintain.routes.CheckDetailsController.onSubmit().url
  private lazy val onwardRoute = frontendAppConfig.maintainATrustOverviewUrl

  private val mockTrustsConnector: TrustsConnector = mock[TrustsConnector]
  private val mockTrustsStoreConnector: TrustsStoreConnector = mock[TrustsStoreConnector]

  override def beforeEach(): Unit = {
    reset(mockTrustsConnector, mockTrustsStoreConnector)

    when(mockTrustsConnector.getTrustDetails(any())(any(), any()))
      .thenReturn(Future.successful(oldTrustDetails()))

    when(mockTrustsConnector.removeTrustTypeDependentTransformFields(any())(any(), any()))
      .thenReturn(Future.successful(HttpResponse(OK, "")))

    when(mockTrustsConnector.removeOptionalTrustDetailTransforms(any())(any(), any()))
      .thenReturn(Future.successful(HttpResponse(OK, "")))

    when(mockTrustsConnector.setMigratingTrustDetails(any(), any())(any(), any()))
      .thenReturn(Future.successful(HttpResponse(OK, "")))

    when(mockTrustsConnector.setNonMigratingTrustDetails(any(), any())(any(), any()))
      .thenReturn(Future.successful(HttpResponse(OK, "")))

    when(mockTrustsStoreConnector.updateTaskStatus(any(), any())(any(), any()))
      .thenReturn(Future.successful(HttpResponse(OK, "")))
  }

  private val migratingTrustDetails = MigratingTrustDetails(
    lawCountry = None,
    administrationCountry = "GB",
    residentialStatus = ResidentialStatusType(uk = Some(UkType(scottishLaw = true, preOffShore = None))),
    trustUKProperty = true,
    trustRecorded = true,
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

  private def oldTrustDetails(typeOfTrust: TypeOfTrust = EmploymentRelated) = TrustDetailsType(
    startDate = LocalDate.parse("2000-01-01"),
    lawCountry = None,
    administrationCountry = None,
    residentialStatus = None,
    trustUKProperty = None,
    trustRecorded = None,
    trustUKRelation = None,
    trustUKResident = None,
    typeOfTrust = Some(typeOfTrust),
    deedOfVariation = None,
    interVivos = None,
    efrbsStartDate = None,
    schedule3aExempt = None
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

          val mockMapper = mock[TrustDetailsMapper]

          val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
            .overrides(
              bind[TrustsConnector].toInstance(mockTrustsConnector),
              bind[TrustDetailsMapper].toInstance(mockMapper),
              bind[TrustsStoreConnector].toInstance(mockTrustsStoreConnector)
            ).build()

          val trustDetails = nonMigratingTrustDetails
          when(mockMapper(any())).thenReturn(JsSuccess(trustDetails))

          val request = FakeRequest(POST, submitDetailsRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute

          verify(mockTrustsConnector, never).removeOptionalTrustDetailTransforms(ArgumentMatchers.eq(userAnswers.identifier))(any(), any())
          verify(mockTrustsConnector).setNonMigratingTrustDetails(ArgumentMatchers.eq(userAnswers.identifier), ArgumentMatchers.eq(trustDetails))(any(), any())
          verify(mockTrustsStoreConnector).updateTaskStatus(ArgumentMatchers.eq(userAnswers.identifier), ArgumentMatchers.eq(Completed))(any(), any())

          application.stop()
        }

        "migrating" in {

          val userAnswers = emptyUserAnswers.copy(migratingFromNonTaxableToTaxable = true)

          val mockMapper = mock[TrustDetailsMapper]

          val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
            .overrides(
              bind[TrustsConnector].toInstance(mockTrustsConnector),
              bind[TrustDetailsMapper].toInstance(mockMapper),
              bind[TrustsStoreConnector].toInstance(mockTrustsStoreConnector)
            ).build()

          val trustDetails = migratingTrustDetails
          when(mockMapper(any())).thenReturn(JsSuccess(trustDetails))

          val request = FakeRequest(POST, submitDetailsRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute

          verify(mockTrustsConnector).removeOptionalTrustDetailTransforms(ArgumentMatchers.eq(userAnswers.identifier))(any(), any())
          verify(mockTrustsConnector).setMigratingTrustDetails(ArgumentMatchers.eq(userAnswers.identifier), ArgumentMatchers.eq(trustDetails))(any(), any())
          verify(mockTrustsStoreConnector).updateTaskStatus(ArgumentMatchers.eq(userAnswers.identifier), ArgumentMatchers.eq(Completed))(any(), any())

          application.stop()
        }
      }

      "remove trust type dependent transform fields" when {
        "previous trust type is EmploymentRelated and new one is different" in {

          forAll(arbitrary[TypeOfTrust].suchThat(_ != EmploymentRelated)) {
            newAnswer =>
              beforeEach()

              val userAnswers = emptyUserAnswers

              val mockMapper = mock[TrustDetailsMapper]

              val application = applicationBuilder(userAnswers = Some(userAnswers))
                .overrides(
                  bind[TrustsConnector].toInstance(mockTrustsConnector),
                  bind[TrustDetailsMapper].toInstance(mockMapper),
                  bind[TrustsStoreConnector].toInstance(mockTrustsStoreConnector)
                ).build()

              val newTrustDetails = migratingTrustDetails.copy(typeOfTrust = newAnswer)
              when(mockMapper(any())).thenReturn(JsSuccess(newTrustDetails))

              val request = FakeRequest(POST, submitDetailsRoute)

              val result = route(application, request).value

              status(result) mustEqual SEE_OTHER

              redirectLocation(result).value mustEqual onwardRoute

              verify(mockTrustsConnector).removeTrustTypeDependentTransformFields(any())(any(), any())
              verify(mockTrustsConnector).setMigratingTrustDetails(ArgumentMatchers.eq(userAnswers.identifier), ArgumentMatchers.eq(newTrustDetails))(any(), any())
              verify(mockTrustsStoreConnector).updateTaskStatus(ArgumentMatchers.eq(userAnswers.identifier), ArgumentMatchers.eq(Completed))(any(), any())

              application.stop()
          }
        }
      }

      "not remove trust type dependent transform fields" when {

        "previous trust type is not EmploymentRelated and new one is anything" in {

          forAll(arbitrary[TypeOfTrust].suchThat(_ != EmploymentRelated), arbitrary[TypeOfTrust]) {
            (oldAnswer, newAnswer) =>
              beforeEach()

              val userAnswers = emptyUserAnswers

              val mockMapper = mock[TrustDetailsMapper]

              val application = applicationBuilder(userAnswers = Some(userAnswers))
                .overrides(
                  bind[TrustsConnector].toInstance(mockTrustsConnector),
                  bind[TrustDetailsMapper].toInstance(mockMapper),
                  bind[TrustsStoreConnector].toInstance(mockTrustsStoreConnector)
                ).build()

              when(mockTrustsConnector.getTrustDetails(any())(any(), any()))
                .thenReturn(Future.successful(oldTrustDetails(oldAnswer)))

              val newTrustDetails = migratingTrustDetails.copy(typeOfTrust = newAnswer)
              when(mockMapper(any())).thenReturn(JsSuccess(newTrustDetails))

              val request = FakeRequest(POST, submitDetailsRoute)

              val result = route(application, request).value

              status(result) mustEqual SEE_OTHER

              redirectLocation(result).value mustEqual onwardRoute

              verify(mockTrustsConnector, never).removeTrustTypeDependentTransformFields(any())(any(), any())
              verify(mockTrustsConnector).setMigratingTrustDetails(ArgumentMatchers.eq(userAnswers.identifier), ArgumentMatchers.eq(newTrustDetails))(any(), any())
              verify(mockTrustsStoreConnector).updateTaskStatus(ArgumentMatchers.eq(userAnswers.identifier), ArgumentMatchers.eq(Completed))(any(), any())

              application.stop()
          }
        }

        "previous trust type is EmploymentRelated and new one hasn't changed" in {

          val userAnswers = emptyUserAnswers

          val mockMapper = mock[TrustDetailsMapper]

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(
              bind[TrustsConnector].toInstance(mockTrustsConnector),
              bind[TrustDetailsMapper].toInstance(mockMapper),
              bind[TrustsStoreConnector].toInstance(mockTrustsStoreConnector)
            ).build()

          val newTrustDetails = migratingTrustDetails.copy(typeOfTrust = EmploymentRelated)
          when(mockMapper(any())).thenReturn(JsSuccess(newTrustDetails))

          val request = FakeRequest(POST, submitDetailsRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute

          verify(mockTrustsConnector, never).removeTrustTypeDependentTransformFields(any())(any(), any())
          verify(mockTrustsConnector).setMigratingTrustDetails(ArgumentMatchers.eq(userAnswers.identifier), ArgumentMatchers.eq(newTrustDetails))(any(), any())
          verify(mockTrustsStoreConnector).updateTaskStatus(ArgumentMatchers.eq(userAnswers.identifier), ArgumentMatchers.eq(Completed))(any(), any())

          application.stop()
        }
      }

      "return internal server error" when {

        "error mapping answers" in {

          val mockMapper = mock[TrustDetailsMapper]

          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), affinityGroup = Agent)
            .overrides(
              bind[TrustsConnector].toInstance(mockTrustsConnector),
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

            val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), affinityGroup = Agent)
              .overrides(
                bind[TrustsConnector].toInstance(mockTrustsConnector),
                bind[TrustDetailsMapper].toInstance(mockMapper)
              ).build()

            when(mockMapper(any())).thenReturn(JsSuccess(nonMigratingTrustDetails))

            when(mockTrustsConnector.setNonMigratingTrustDetails(any(), any())(any(), any())).thenReturn(Future.failed(new Throwable("")))

            val request = FakeRequest(POST, submitDetailsRoute)

            val result = route(application, request).value

            status(result) mustEqual INTERNAL_SERVER_ERROR

            application.stop()
          }

          "migrating" in {

            val mockMapper = mock[TrustDetailsMapper]

            val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), affinityGroup = Agent)
              .overrides(
                bind[TrustsConnector].toInstance(mockTrustsConnector),
                bind[TrustDetailsMapper].toInstance(mockMapper)
              ).build()

            when(mockMapper(any())).thenReturn(JsSuccess(migratingTrustDetails))

            when(mockTrustsConnector.setMigratingTrustDetails(any(), any())(any(), any())).thenReturn(Future.failed(new Throwable("")))

            val request = FakeRequest(POST, submitDetailsRoute)

            val result = route(application, request).value

            status(result) mustEqual INTERNAL_SERVER_ERROR

            application.stop()
          }
        }

        "error amending transforms" in {

          val mockMapper = mock[TrustDetailsMapper]

          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(
              bind[TrustsConnector].toInstance(mockTrustsConnector),
              bind[TrustDetailsMapper].toInstance(mockMapper)
            ).build()

          val newTrustDetails = migratingTrustDetails.copy(typeOfTrust = HeritageMaintenanceFund)
          when(mockMapper(any())).thenReturn(JsSuccess(newTrustDetails))

          when(mockTrustsConnector.removeTrustTypeDependentTransformFields(any())(any(), any())).thenReturn(Future.failed(new Throwable("")))

          val request = FakeRequest(POST, submitDetailsRoute)

          val result = route(application, request).value

          status(result) mustEqual INTERNAL_SERVER_ERROR

          application.stop()
        }
      }
    }

  }
}
