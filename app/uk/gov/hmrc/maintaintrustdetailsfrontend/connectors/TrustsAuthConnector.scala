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

package uk.gov.hmrc.maintaintrustdetailsfrontend.connectors

import com.google.inject.ImplementedBy
import play.api.Logging
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import uk.gov.hmrc.maintaintrustdetailsfrontend.config.AppConfig
import uk.gov.hmrc.maintaintrustdetailsfrontend.models.http.{TrustsAuthInternalServerError, TrustsAuthResponse}
import uk.gov.hmrc.maintaintrustdetailsfrontend.utils.Session

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[TrustsAuthConnectorImpl])
trait TrustsAuthConnector {
  def agentIsAuthorised()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustsAuthResponse]
  def authorisedForIdentifier(identifier: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustsAuthResponse]
}

class TrustsAuthConnectorImpl @Inject()(http: HttpClient, config: AppConfig)
  extends TrustsAuthConnector with Logging {

  private val baseUrl: String = config.trustsAuthUrl + "/trusts-auth"

  override def agentIsAuthorised()
                                (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustsAuthResponse] = {
    http.GET[TrustsAuthResponse](s"$baseUrl/agent-authorised").recoverWith {
      case e =>
        logger.warn(s"[Session ID: ${Session.id}] unable to authenticate agent due to an exception ${e.getMessage}")
        Future.successful(TrustsAuthInternalServerError)
    }
  }

  override def authorisedForIdentifier(identifier: String)
                                      (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustsAuthResponse] = {
    http.GET[TrustsAuthResponse](s"$baseUrl/authorised/$identifier").recoverWith {
      case e =>
        logger.warn(s"[Session ID: ${Session.id}] unable to authenticate organisation for $identifier due to an exception ${e.getMessage}")
        Future.successful(TrustsAuthInternalServerError)
    }
  }
}