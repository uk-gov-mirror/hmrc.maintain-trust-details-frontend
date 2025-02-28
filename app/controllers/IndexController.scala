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

package controllers

import config.{AppConfig, ErrorHandler}
import connectors.TrustsConnector
import controllers.actions.StandardActionSets
import extractors.TrustDetailsExtractor
import mappers.TrustDetailsMapper
import models.TaskStatus.InProgress
import models.UserAnswers
import play.api.mvc._
import repositories.PlaybackRepository
import services.TrustsStoreService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.{Session, SessionLogging}
import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

@Singleton
class IndexController @Inject()(
                                 mcc: MessagesControllerComponents,
                                 actions: StandardActionSets,
                                 trustsStoreService: TrustsStoreService,
                                 cacheRepository: PlaybackRepository,
                                 appConfig: AppConfig,
                                 connector: TrustsConnector,
                                 extractor: TrustDetailsExtractor,
                                 mapper: TrustDetailsMapper,
                                 errorHandler: ErrorHandler
                               )(implicit ec: ExecutionContext) extends FrontendController(mcc) with SessionLogging {

  def onPageLoad(identifier: String): Action[AnyContent] = actions.authWithSavedSession(identifier).async {
    implicit request =>

      (for {
        trustDetails <- connector.getTrustDetails(identifier)
        taxableMigrationFlag <- connector.getTrustMigrationFlag(identifier)
        registeredWithDeceasedSettlor <- connector.wasTrustRegisteredWithDeceasedSettlor(identifier)
        trustName <- connector.getTrustName(identifier)
        ua <- Future.fromTry {
          request.userAnswers match {
            case Some(userAnswers) if userAnswers.migratingFromNonTaxableToTaxable == taxableMigrationFlag.migratingFromNonTaxableToTaxable =>
              infoLog("User is on the same type of journey as before. Persisting answers.", Some(identifier))
              Success(userAnswers)
            case _ =>
              extractor(
                answers = UserAnswers(
                  internalId = request.user.internalId,
                  identifier = identifier,
                  sessionId = Session.id(hc),
                  newId = s"${request.user.internalId}-$identifier-${Session.id(hc)}",
                  migratingFromNonTaxableToTaxable = taxableMigrationFlag.migratingFromNonTaxableToTaxable,
                  registeredWithDeceasedSettlor = registeredWithDeceasedSettlor
                ),
                trustDetails = trustDetails,
                trustName = trustName
              )
          }
        }
        _ <- cacheRepository.set(ua)
        _ <- trustsStoreService.updateTaskStatus(identifier, InProgress)
      } yield {
          if (mapper.areAnswersSubmittable(ua)) {
            Redirect(controllers.maintain.routes.CheckDetailsController.onPageLoad())
          } else {
            if (taxableMigrationFlag.migratingFromNonTaxableToTaxable) {
              Redirect(controllers.maintain.routes.GovernedByUkLawController.onPageLoad())
            } else {
              Redirect(controllers.maintain.routes.BeforeYouContinueController.onPageLoad())
            }
          }
      }) recoverWith {
        case e =>
          errorLog(s"Error setting up session: ${e.getMessage}", Some(identifier))
          errorHandler.internalServerErrorTemplate.map(html => InternalServerError(html))
      }
  }

}
