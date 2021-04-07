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

import com.google.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.maintaintrustdetailsfrontend.config.AppConfig
import uk.gov.hmrc.maintaintrustdetailsfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import uk.gov.hmrc.maintaintrustdetailsfrontend.utils.{Session, SessionLogging}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.ExecutionContext

@Singleton
class LogoutController @Inject()(
                                  appConfig: AppConfig,
                                  val controllerComponents: MessagesControllerComponents,
                                  identify: IdentifierAction,
                                  getData: DataRetrievalAction,
                                  requireData: DataRequiredAction,
                                  auditConnector: AuditConnector
                                )(implicit val ec: ExecutionContext) extends FrontendBaseController with SessionLogging {

  def logout: Action[AnyContent] = (identify andThen getData andThen requireData) {
    request =>

      implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

      infoLog(request.userAnswers.identifier, "user signed out from the service, asking for feedback")

      if(appConfig.logoutAudit) {

        val auditData = Map(
          "sessionId" -> Session.id,
          "event" -> "signout",
          "service" -> "maintain-trust-details-frontend",
          "userGroup" -> request.user.affinityGroup.toString,
          "utr" -> request.userAnswers.identifier
        )

        auditConnector.sendExplicitAudit(
          "trusts",
          auditData
        )

      }

      Redirect(appConfig.logoutUrl).withSession(session = ("feedbackId", Session.id))
  }
}
