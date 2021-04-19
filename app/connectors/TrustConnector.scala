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

  def getTrustDetails(identifier: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[TrustDetails] = {
    val url = s"${config.trustsUrl}/trusts/trust-details/$identifier/transformed"
    http.GET[TrustDetails](url)
  }

  def setUkProperty(identifier: String, answer: Boolean)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    val url = s"${config.trustsUrl}/trusts/trust-details/$identifier/uk-property"
    http.PUT(url, JsBoolean(answer))
  }

  def setUkRelation(identifier: String, answer: Boolean)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    val url = s"${config.trustsUrl}/trusts/trust-details/$identifier/uk-relation"
    http.PUT(url, JsBoolean(answer))
  }

  def setTrustRecorded(identifier: String, answer: Boolean)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    val url = s"${config.trustsUrl}/trusts/trust-details/$identifier/recorded"
    http.PUT(url, JsBoolean(answer))
  }

}
