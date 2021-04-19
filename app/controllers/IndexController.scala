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

import play.api.mvc._
import config.AppConfig
import connectors.TrustsConnector
import controllers.actions.StandardActionSets
import extractors.TrustDetailsExtractor
import models.UserAnswers
import repositories.PlaybackRepository
import services.FeatureFlagService
import utils.SessionLogging
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

@Singleton
class IndexController @Inject()(
                                 mcc: MessagesControllerComponents,
                                 actions: StandardActionSets,
                                 featureFlagService: FeatureFlagService,
                                 cacheRepository: PlaybackRepository,
                                 appConfig: AppConfig,
                                 connector: TrustsConnector,
                                 extractor: TrustDetailsExtractor
                               )(implicit ec: ExecutionContext) extends FrontendController(mcc) with SessionLogging {

  def onPageLoad(identifier: String): Action[AnyContent] = actions.authWithSavedSession(identifier).async {
    implicit request =>

      for {
        is5mldEnabled <- featureFlagService.is5mldEnabled()
        trustDetails <- connector.getTrustDetails(identifier)
        ua <- Future.fromTry {
          request.userAnswers match {
            case Some(userAnswers) => Success(userAnswers)
            case None => extractor(UserAnswers(request.user.internalId, identifier), trustDetails)
          }
        }
        _ <- cacheRepository.set(ua)
      } yield {
        if (is5mldEnabled) {
          Redirect(controllers.maintain.routes.TrustOwnUKLandOrPropertyController.onPageLoad())
        } else {
          warnLog("Service is not in 5MLD mode. Redirecting to task list.", Some(identifier))
          Redirect(appConfig.maintainATrustOverviewUrl)
        }
      }
  }

}
