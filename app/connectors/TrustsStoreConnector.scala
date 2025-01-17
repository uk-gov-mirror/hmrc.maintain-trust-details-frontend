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

import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}
import config.AppConfig
import models.http.FeatureResponse

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TrustsStoreConnector @Inject()(http: HttpClient, config: AppConfig) {

  def setTaskComplete(identifier: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"${config.trustsStoreUrl}/trusts-store/maintain/tasks/trust-details/$identifier"
    http.POSTEmpty[HttpResponse](url)
  }

  def getFeature(feature: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[FeatureResponse] = {
    val url: String = s"${config.trustsStoreUrl}/trusts-store/features/$feature"
    http.GET[FeatureResponse](url)
  }

}
