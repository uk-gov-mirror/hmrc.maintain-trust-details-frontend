/*
 * Copyright 2022 HM Revenue & Customs
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
import models.TaskStatus.Completed
import models.TypeOfTrust.EmploymentRelated
import models._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsError, JsSuccess}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
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
        case JsSuccess(newTrustDetails, _) =>
          (for {
            oldTrustDetails <- connector.getTrustDetails(identifier)
            _ <- removeOptionalTransformsIfMigrating(userAnswers.migratingFromNonTaxableToTaxable, identifier)
            _ <- setNewDetails(newTrustDetails, identifier)
            _ <- removeAnyTrustTypeDependentTransformFields(newTrustDetails, oldTrustDetails, identifier)
            _ <- trustsStoreConnector.updateTaskStatus(request.userAnswers.identifier, Completed)
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

  private def removeOptionalTransformsIfMigrating(migratingFromNonTaxableToTaxable: Boolean, identifier: String)
                                                 (implicit hc: HeaderCarrier): Future[Unit] = {
    if (migratingFromNonTaxableToTaxable) {
      connector.removeOptionalTrustDetailTransforms(identifier).map(_ => ())
    } else {
      Future.successful(())
    }
  }

  private def setNewDetails(newTrustDetails: TrustDetails, identifier: String)
                           (implicit hc: HeaderCarrier): Future[HttpResponse] = {
    newTrustDetails match {
      case x: NonMigratingTrustDetails => connector.setNonMigratingTrustDetails(identifier, x)
      case x: MigratingTrustDetails => connector.setMigratingTrustDetails(identifier, x)
    }
  }

  private def removeAnyTrustTypeDependentTransformFields(newTrustDetails: TrustDetails, oldTrustDetails: TrustDetailsType, identifier: String)
                                                        (implicit hc: HeaderCarrier): Future[Unit] = {

    def needToRemoveTrustTypeDependentTransformFields(previousAnswer: Option[TypeOfTrust], newAnswer: TypeOfTrust): Boolean = {
      previousAnswer match {
        case Some(x) => x == EmploymentRelated && x != newAnswer
        case _ => false
      }
    }

    newTrustDetails match {
      case x: MigratingTrustDetails if needToRemoveTrustTypeDependentTransformFields(oldTrustDetails.typeOfTrust, x.typeOfTrust) =>
        connector.removeTrustTypeDependentTransformFields(identifier).map(_ => ())
      case _ =>
        Future.successful(())
    }
  }

}
