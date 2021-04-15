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

package connectors

import config.AppConfig
import javax.inject.Inject
import models.TrustDetails
import play.api.libs.json.JsBoolean
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

class TrustConnector @Inject()(http: HttpClient, config : AppConfig) {

  implicit val legacyRawReads: HttpReads[HttpResponse] = HttpReads.Implicits.throwOnFailure(HttpReads.Implicits.readEitherOf(HttpReads.Implicits.readRaw))

  private def getTrustDetailsUrl(identifier: String) = s"${config.trustsUrl}/trusts/$identifier/trust-details"

  def getTrustDetails(identifier: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[TrustDetails] =
    http.GET[TrustDetails](getTrustDetailsUrl(identifier))

  private def amendPropertyOrLandUrl(identifier: String) = s"${config.trustsUrl}/trusts/$identifier/trust-details/uk-property"

  def amendPropertyOrLand(identifier: String, answer: Boolean)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] =
    http.PUT(amendPropertyOrLandUrl(identifier), JsBoolean(answer))

  private def businessRelationshipYesNoUrl(identifier: String) = s"${config.trustsUrl}/trusts/$identifier/trust-details/uk-relation "

  def amendBusinessRelationshipYesNo(identifier: String, answer: Boolean)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] =
    http.PUT(businessRelationshipYesNoUrl(identifier), JsBoolean(answer))

  private def trustEEAYesNoUrl(identifier: String) = s"${config.trustsUrl}/trusts/$identifier/trust-details/???"

  def trustEEAYesNo(identifier: String, answer: Boolean)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] =
    http.PUT(trustEEAYesNoUrl(identifier), JsBoolean(answer))

}
