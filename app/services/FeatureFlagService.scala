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

package services

import uk.gov.hmrc.http.HeaderCarrier
import connectors.TrustsStoreConnector
import models.http.FeatureResponse

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FeatureFlagService @Inject()(trustsStoreConnector: TrustsStoreConnector) {

  def is5mldEnabled()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] = {
    isFeatureEnabled("5mld")
  }

  private def isFeatureEnabled(feature: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] = {
    trustsStoreConnector.getFeature(feature).map {
      case FeatureResponse(_, true) => true
      case _ => false
    }
  }
  
}
