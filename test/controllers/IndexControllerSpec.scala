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
import models.TrustDetailsType
import org.mockito.Matchers.any
import org.mockito.Mockito.{never, reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.FeatureFlagService

import java.time.LocalDate
import scala.concurrent.Future
import scala.util.Success

class IndexControllerSpec extends SpecBase with BeforeAndAfterEach {

  val mockFeatureFlagService: FeatureFlagService = mock[FeatureFlagService]
  val mockTrustsConnector: TrustsConnector = mock[TrustsConnector]
  val mockExtractor: TrustDetailsExtractor = mock[TrustDetailsExtractor]

  val onPageLoad: String = routes.IndexController.onPageLoad(identifier).url

  val fakeTrustDetails: TrustDetailsType =
    TrustDetailsType(LocalDate.parse("2020-01-01"), None, None, None, None, None, None)

  override def beforeEach(): Unit = {
    reset(mockFeatureFlagService)

    reset(mockTrustsConnector)
    when(mockTrustsConnector.getTrustDetails(any())(any(), any()))
      .thenReturn(Future.successful(fakeTrustDetails))

    reset(mockExtractor)
    when(mockExtractor(any(), any())).thenReturn(Success(emptyUserAnswers))
  }

  "IndexController" when {

    "4mld and no previous answers" must {
      "call extractor and redirect to task list" in {

        when(mockFeatureFlagService.is5mldEnabled()(any(), any()))
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

        verify(mockExtractor).apply(any(), any())

        application.stop()
      }
    }

    "5mld and some previous answers" must {
      "not call extractor and redirect to TrustOwnUKLandOrPropertyController" in {

        when(mockFeatureFlagService.is5mldEnabled()(any(), any()))
          .thenReturn(Future.successful(true))

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[FeatureFlagService].toInstance(mockFeatureFlagService),
            bind[TrustsConnector].toInstance(mockTrustsConnector),
            bind[TrustDetailsExtractor].toInstance(mockExtractor)
          ).build()

        val request = FakeRequest(GET, onPageLoad)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustBe controllers.maintain.routes.TrustOwnUKLandOrPropertyController.onPageLoad().url

        verify(mockExtractor, never()).apply(any(), any())

        application.stop()
      }
    }
  }
}