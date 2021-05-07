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

package controllers

import base.SpecBase
import connectors.TrustsConnector
import controllers.Assets.SEE_OTHER
import extractors.TrustDetailsExtractor
import generators.ModelGenerators
import models.{TrustDetailsType, UserAnswers}
import models.http.TaxableMigrationFlag
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{never, reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.FeatureFlagService
import utils.UserAnswersStatus

import java.time.LocalDate
import scala.concurrent.Future
import scala.util.{Failure, Success}

class IndexControllerSpec extends SpecBase with BeforeAndAfterEach with ScalaCheckPropertyChecks with ModelGenerators {

  val mockFeatureFlagService: FeatureFlagService = mock[FeatureFlagService]
  val mockTrustsConnector: TrustsConnector = mock[TrustsConnector]
  val mockExtractor: TrustDetailsExtractor = mock[TrustDetailsExtractor]

  val onPageLoad: String = routes.IndexController.onPageLoad(identifier).url

  val fakeTrustDetails: TrustDetailsType =
    TrustDetailsType(LocalDate.parse("2020-01-01"), None, None, None, None, None, None, None, None, None, None, None)

  override def beforeEach(): Unit = {
    reset(mockFeatureFlagService)

    reset(mockTrustsConnector)
    when(mockTrustsConnector.getTrustDetails(any())(any(), any()))
      .thenReturn(Future.successful(fakeTrustDetails))

    reset(mockExtractor)
    when(mockExtractor(any(), any())).thenReturn(Success(emptyUserAnswers))

    reset(playbackRepository)
    when(playbackRepository.set(any())).thenReturn(Future.successful(true))
  }

  "IndexController" when {

    "4mld" must {
      "redirect to task list" in {

        when(mockFeatureFlagService.is5mldEnabled()(any(), any()))
          .thenReturn(Future.successful(false))

        when(mockTrustsConnector.getTrustMigrationFlag(any())(any(), any()))
          .thenReturn(Future.successful(TaxableMigrationFlag(None)))

        when(mockTrustsConnector.wasTrustRegisteredWithDeceasedSettlor(any())(any(), any()))
          .thenReturn(Future.successful(false))

        val application = applicationBuilder(userAnswers = None)
          .overrides(
            bind[FeatureFlagService].toInstance(mockFeatureFlagService),
            bind[TrustsConnector].toInstance(mockTrustsConnector),
            bind[TrustDetailsExtractor].toInstance(mockExtractor)
          ).build()

        val request = FakeRequest(GET, onPageLoad)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustBe frontendAppConfig.maintainATrustOverviewUrl

        application.stop()
      }
    }

    "5mld" when {
      "no previous answers" must {
        "call extractor" in {

          forAll(arbitrary[TaxableMigrationFlag], arbitrary[Boolean]) {
            (taxableMigrationFlag, registeredWithDeceasedSettlor) =>

              beforeEach()

              when(mockFeatureFlagService.is5mldEnabled()(any(), any()))
                .thenReturn(Future.successful(true))

              when(mockTrustsConnector.getTrustMigrationFlag(any())(any(), any()))
                .thenReturn(Future.successful(taxableMigrationFlag))

              when(mockTrustsConnector.wasTrustRegisteredWithDeceasedSettlor(any())(any(), any()))
                .thenReturn(Future.successful(registeredWithDeceasedSettlor))

              val application = applicationBuilder(userAnswers = None)
                .overrides(
                  bind[FeatureFlagService].toInstance(mockFeatureFlagService),
                  bind[TrustsConnector].toInstance(mockTrustsConnector),
                  bind[TrustDetailsExtractor].toInstance(mockExtractor)
                ).build()

              val request = FakeRequest(GET, onPageLoad)

              val result = route(application, request).value

              status(result) mustEqual SEE_OTHER

              val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
              verify(mockExtractor).apply(uaCaptor.capture, any())
              uaCaptor.getValue.migratingFromNonTaxableToTaxable mustBe taxableMigrationFlag.migratingFromNonTaxableToTaxable
              uaCaptor.getValue.registeredWithDeceasedSettlor mustBe registeredWithDeceasedSettlor
          }
        }
      }

      "previous answers" when {

        "value of migratingFromNonTaxableToTaxable has changed" must {
          "call extractor" in {

            forAll(arbitrary[TaxableMigrationFlag], arbitrary[Boolean]) {
              (taxableMigrationFlag, registeredWithDeceasedSettlor) =>

                beforeEach()

                when(mockFeatureFlagService.is5mldEnabled()(any(), any()))
                  .thenReturn(Future.successful(true))

                when(mockTrustsConnector.getTrustMigrationFlag(any())(any(), any()))
                  .thenReturn(Future.successful(taxableMigrationFlag))

                when(mockTrustsConnector.wasTrustRegisteredWithDeceasedSettlor(any())(any(), any()))
                  .thenReturn(Future.successful(registeredWithDeceasedSettlor))

                val application = applicationBuilder(
                  userAnswers = Some(emptyUserAnswers.copy(
                    migratingFromNonTaxableToTaxable = !taxableMigrationFlag.migratingFromNonTaxableToTaxable,
                    registeredWithDeceasedSettlor = registeredWithDeceasedSettlor
                  ))
                ).overrides(
                  bind[FeatureFlagService].toInstance(mockFeatureFlagService),
                  bind[TrustsConnector].toInstance(mockTrustsConnector),
                  bind[TrustDetailsExtractor].toInstance(mockExtractor)
                ).build()

                val request = FakeRequest(GET, onPageLoad)

                val result = route(application, request).value

                status(result) mustEqual SEE_OTHER

                val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
                verify(mockExtractor).apply(uaCaptor.capture, any())
                uaCaptor.getValue.migratingFromNonTaxableToTaxable mustBe taxableMigrationFlag.migratingFromNonTaxableToTaxable
                uaCaptor.getValue.registeredWithDeceasedSettlor mustBe registeredWithDeceasedSettlor
            }
          }
        }

        "value of migratingFromNonTaxableToTaxable has not changed" must {
          "not call extractor" when {

            val mockUserAnswersStatus = mock[UserAnswersStatus]

            "in submittable state" must {
              "redirect to CheckDetailsController" in {

                forAll(arbitrary[TaxableMigrationFlag], arbitrary[Boolean]) {
                  (taxableMigrationFlag, registeredWithDeceasedSettlor) =>

                    beforeEach()

                    when(mockFeatureFlagService.is5mldEnabled()(any(), any()))
                      .thenReturn(Future.successful(true))

                    when(mockTrustsConnector.getTrustMigrationFlag(any())(any(), any()))
                      .thenReturn(Future.successful(taxableMigrationFlag))

                    when(mockTrustsConnector.wasTrustRegisteredWithDeceasedSettlor(any())(any(), any()))
                      .thenReturn(Future.successful(registeredWithDeceasedSettlor))

                    when(mockUserAnswersStatus.areAnswersSubmittable(any(), any()))
                      .thenReturn(true)

                    val application = applicationBuilder(
                      userAnswers = Some(emptyUserAnswers.copy(
                        migratingFromNonTaxableToTaxable = taxableMigrationFlag.migratingFromNonTaxableToTaxable,
                        registeredWithDeceasedSettlor = registeredWithDeceasedSettlor
                      ))
                    ).overrides(
                      bind[FeatureFlagService].toInstance(mockFeatureFlagService),
                      bind[TrustsConnector].toInstance(mockTrustsConnector),
                      bind[TrustDetailsExtractor].toInstance(mockExtractor),
                      bind[UserAnswersStatus].toInstance(mockUserAnswersStatus)
                    ).build()

                    val request = FakeRequest(GET, onPageLoad)

                    val result = route(application, request).value

                    status(result) mustEqual SEE_OTHER

                    redirectLocation(result).value mustBe controllers.maintain.routes.CheckDetailsController.onPageLoad().url

                    verify(mockExtractor, never()).apply(any(), any())

                    val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
                    verify(playbackRepository).set(uaCaptor.capture)
                    uaCaptor.getValue.migratingFromNonTaxableToTaxable mustBe taxableMigrationFlag.migratingFromNonTaxableToTaxable
                    uaCaptor.getValue.registeredWithDeceasedSettlor mustBe registeredWithDeceasedSettlor
                }
              }
            }

            "not in submittable state" when {

              "migrating from non-taxable to taxable" must {
                "redirect to GovernedByUkLawController" in {

                  forAll(arbitrary[Boolean]) {
                    registeredWithDeceasedSettlor =>

                      beforeEach()

                      when(mockFeatureFlagService.is5mldEnabled()(any(), any()))
                        .thenReturn(Future.successful(true))

                      when(mockTrustsConnector.getTrustMigrationFlag(any())(any(), any()))
                        .thenReturn(Future.successful(TaxableMigrationFlag(Some(true))))

                      when(mockTrustsConnector.wasTrustRegisteredWithDeceasedSettlor(any())(any(), any()))
                        .thenReturn(Future.successful(registeredWithDeceasedSettlor))

                      when(mockUserAnswersStatus.areAnswersSubmittable(any(), any()))
                        .thenReturn(false)

                      val application = applicationBuilder(
                        userAnswers = Some(emptyUserAnswers.copy(
                          migratingFromNonTaxableToTaxable = true,
                          registeredWithDeceasedSettlor = registeredWithDeceasedSettlor
                        ))
                      ).overrides(
                        bind[FeatureFlagService].toInstance(mockFeatureFlagService),
                        bind[TrustsConnector].toInstance(mockTrustsConnector),
                        bind[TrustDetailsExtractor].toInstance(mockExtractor),
                        bind[UserAnswersStatus].toInstance(mockUserAnswersStatus)
                      ).build()

                      val request = FakeRequest(GET, onPageLoad)

                      val result = route(application, request).value

                      status(result) mustEqual SEE_OTHER

                      redirectLocation(result).value mustBe controllers.routes.FeatureNotAvailableController.onPageLoad().url

                      verify(mockExtractor, never()).apply(any(), any())

                      val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
                      verify(playbackRepository).set(uaCaptor.capture)
                      uaCaptor.getValue.migratingFromNonTaxableToTaxable mustBe true
                      uaCaptor.getValue.registeredWithDeceasedSettlor mustBe registeredWithDeceasedSettlor
                  }
                }
              }

              "not migrating" must {
                "redirect to BeforeYouContinueController" in {

                  forAll(arbitrary[TaxableMigrationFlag].suchThat(!_.value.contains(true)), arbitrary[Boolean]) {
                    (taxableMigrationFlag, registeredWithDeceasedSettlor) =>

                      beforeEach()

                      when(mockFeatureFlagService.is5mldEnabled()(any(), any()))
                        .thenReturn(Future.successful(true))

                      when(mockTrustsConnector.getTrustMigrationFlag(any())(any(), any()))
                        .thenReturn(Future.successful(taxableMigrationFlag))

                      when(mockTrustsConnector.wasTrustRegisteredWithDeceasedSettlor(any())(any(), any()))
                        .thenReturn(Future.successful(registeredWithDeceasedSettlor))

                      when(mockUserAnswersStatus.areAnswersSubmittable(any(), any()))
                        .thenReturn(false)

                      val application = applicationBuilder(
                        userAnswers = Some(emptyUserAnswers.copy(
                          migratingFromNonTaxableToTaxable = taxableMigrationFlag.migratingFromNonTaxableToTaxable,
                          registeredWithDeceasedSettlor = registeredWithDeceasedSettlor
                        ))
                      ).overrides(
                        bind[FeatureFlagService].toInstance(mockFeatureFlagService),
                        bind[TrustsConnector].toInstance(mockTrustsConnector),
                        bind[TrustDetailsExtractor].toInstance(mockExtractor),
                        bind[UserAnswersStatus].toInstance(mockUserAnswersStatus)
                      ).build()

                      val request = FakeRequest(GET, onPageLoad)

                      val result = route(application, request).value

                      status(result) mustEqual SEE_OTHER

                      redirectLocation(result).value mustBe controllers.maintain.routes.BeforeYouContinueController.onPageLoad().url

                      verify(mockExtractor, never()).apply(any(), any())

                      val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
                      verify(playbackRepository).set(uaCaptor.capture)
                      uaCaptor.getValue.migratingFromNonTaxableToTaxable mustBe taxableMigrationFlag.migratingFromNonTaxableToTaxable
                      uaCaptor.getValue.registeredWithDeceasedSettlor mustBe registeredWithDeceasedSettlor
                  }
                }
              }
            }
          }
        }
      }
    }

    "extractor fails" must {
      "return internal server error" in {

        when(mockFeatureFlagService.is5mldEnabled()(any(), any()))
          .thenReturn(Future.successful(true))

        when(mockTrustsConnector.getTrustMigrationFlag(any())(any(), any()))
          .thenReturn(Future.successful(TaxableMigrationFlag(None)))

        when(mockTrustsConnector.wasTrustRegisteredWithDeceasedSettlor(any())(any(), any()))
          .thenReturn(Future.successful(false))

        when(mockExtractor(any(), any())).thenReturn(Failure(new Throwable("")))

        val application = applicationBuilder(userAnswers = None)
          .overrides(
            bind[FeatureFlagService].toInstance(mockFeatureFlagService),
            bind[TrustsConnector].toInstance(mockTrustsConnector),
            bind[TrustDetailsExtractor].toInstance(mockExtractor)
          ).build()

        val request = FakeRequest(GET, onPageLoad)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR

        verify(mockExtractor).apply(any(), any())
      }
    }
  }
}
