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

import play.api.mvc._
import uk.gov.hmrc.maintaintrustdetailsfrontend.config.AppConfig
import uk.gov.hmrc.maintaintrustdetailsfrontend.controllers.actions.StandardActionSets
import uk.gov.hmrc.maintaintrustdetailsfrontend.models.UserAnswers
import uk.gov.hmrc.maintaintrustdetailsfrontend.repositories.PlaybackRepository
import uk.gov.hmrc.maintaintrustdetailsfrontend.services.FeatureFlagService
import uk.gov.hmrc.maintaintrustdetailsfrontend.utils.SessionLogging
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IndexController @Inject()(
                                 mcc: MessagesControllerComponents,
                                 actions: StandardActionSets,
                                 featureFlagService: FeatureFlagService,
                                 cacheRepository: PlaybackRepository,
                                 appConfig: AppConfig
                               )(implicit ec: ExecutionContext) extends FrontendController(mcc) with SessionLogging {

  def onPageLoad(identifier: String): Action[AnyContent] = actions.authWithSavedSession(identifier).async {
    implicit request =>

      for {
        is5mldEnabled <- featureFlagService.is5mldEnabled()
        ua <- Future.successful {
          request.userAnswers match {
            case Some(userAnswers) => userAnswers
            case None => UserAnswers(
              internalId = request.user.internalId,
              identifier = identifier
            )
          }
        }
        _ <- cacheRepository.set(ua)
      } yield {
        if (is5mldEnabled) {
          Redirect(routes.FeatureNotAvailableController.onPageLoad())
        } else {
          warnLog(identifier, "Service is not in 5MLD mode. Redirecting to task list.")
          Redirect(appConfig.maintainATrustOverviewUrl)
        }
      }
  }

}
