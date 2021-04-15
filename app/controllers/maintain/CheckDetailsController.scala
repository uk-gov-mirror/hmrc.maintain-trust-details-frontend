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

import config.AppConfig
import connectors.TrustConnector
import controllers.actions._
import handlers.ErrorHandler
import javax.inject.Inject
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.print.TrustDetailsPrintHelper
import viewmodels.AnswerSection
import views.html.maintain.CheckDetailsView

import scala.concurrent.ExecutionContext

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        standardActionSets: StandardActionSets,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckDetailsView,
                                        connector: TrustConnector,
                                        val appConfig: AppConfig,
                                        printHelper: TrustDetailsPrintHelper,
                                        errorHandler: ErrorHandler
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForIdentifier {
    implicit request =>

      val section: AnswerSection = printHelper(request.userAnswers)
      Ok(view(section))
  }

//  def onSubmit(): Action[AnyContent] = standardActionSets.verifiedForIdentifier.async {
//    implicit request =>
//
//      mapper(request.userAnswers) match {
//        case None =>
//          logger.error(s"[Check Trust Details][identifier: ${request.userAnswers.identifier}][Session ID: ${utils.Session.id(hc)}]" +
//            s" unable to map user answers to TrustDetails due to errors")
//          Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate(request.request)))
//        case Some(otherIndividual) =>
//          connector.addOtherIndividual(request.userAnswers.identifier, otherIndividual).map(_ =>
//            Redirect(controllers.routes.AddAnOtherIndividualController.onPageLoad())
//          )
//      }
//  }
}
