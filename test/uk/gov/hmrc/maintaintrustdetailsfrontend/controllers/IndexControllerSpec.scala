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

package uk.gov.hmrc.maintaintrustdetailsfrontend.controllers

import controllers.Assets.SEE_OTHER
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.maintaintrustdetailsfrontend.base.SpecBase
import uk.gov.hmrc.maintaintrustdetailsfrontend.services.FeatureFlagService

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase {

  val mockFeatureFlagService: FeatureFlagService = mock[FeatureFlagService]

  val onPageLoad: String = routes.IndexController.onPageLoad(identifier).url

  "IndexController" when {

    "4mld" must {
      "redirect to task list" in {

        when(mockFeatureFlagService.is5mldEnabled()(any(), any()))
          .thenReturn(Future.successful(false))

        val application = applicationBuilder(userAnswers = None)
          .overrides(
            bind[FeatureFlagService].toInstance(mockFeatureFlagService)
          ).build()

        val request = FakeRequest(GET, onPageLoad)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustBe frontendAppConfig.maintainATrustOverviewUrl

        application.stop()
      }
    }

    "5mld" must {
      "redirect to feature unavailable" in {

        when(mockFeatureFlagService.is5mldEnabled()(any(), any()))
          .thenReturn(Future.successful(true))

        val application = applicationBuilder(userAnswers = None)
          .overrides(
            bind[FeatureFlagService].toInstance(mockFeatureFlagService)
          ).build()

        val request = FakeRequest(GET, onPageLoad)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustBe routes.FeatureNotAvailableController.onPageLoad().url

        application.stop()
      }
    }
  }
}