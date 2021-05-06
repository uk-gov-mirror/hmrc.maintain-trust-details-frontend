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

import config.{AppConfig, ErrorHandler}
import connectors.{TrustsConnector, TrustsStoreConnector}
import controllers.actions._
import mappers.TrustDetailsMapper
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsError, JsSuccess}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.SessionLogging
import utils.print.TrustDetailsPrintHelper
import viewmodels.AnswerSection
import views.html.maintain.CheckDetailsView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        standardActionSets: StandardActionSets,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckDetailsView,
                                        connector: TrustsConnector,
                                        val appConfig: AppConfig,
                                        printHelper: TrustDetailsPrintHelper,
                                        mapper: TrustDetailsMapper,
                                        errorHandler: ErrorHandler,
                                        trustsStoreConnector: TrustsStoreConnector
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with SessionLogging {

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForIdentifier {
    implicit request =>

      val section: AnswerSection = printHelper(request.userAnswers)
      Ok(view(section))
  }

  def onSubmit(): Action[AnyContent] = standardActionSets.verifiedForIdentifier.async {
    implicit request =>
      val userAnswers = request.userAnswers
      val identifier = userAnswers.identifier

      mapper(userAnswers) match {
        case JsSuccess(trustDetails, _) =>
          (for {
            _ <- connector.setUkProperty(identifier, trustDetails.trustUKProperty)
            _ <- connector.setTrustRecorded(identifier, trustDetails.trustRecorded)
            _ <- trustDetails.trustUKRelation match {
              case Some(value) => connector.setUkRelation(identifier, value)
              case None => Future.successful(())
            }
            _ <- connector.setUkResident(identifier, trustDetails.trustUKResident)
            _ <- trustsStoreConnector.setTaskComplete(request.userAnswers.identifier)
          } yield {
            Redirect(appConfig.maintainATrustOverviewUrl)
          }).recover {
            case e =>
              errorLog(s"Error setting transforms: ${e.getMessage}", Some(identifier))
              InternalServerError(errorHandler.internalServerErrorTemplate)
          }
        case JsError(errors) =>
          errorLog(s"Failed to map user answers: $errors", Some(identifier))
          Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
      }
  }
}
