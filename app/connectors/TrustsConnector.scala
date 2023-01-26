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

package connectors

import config.AppConfig
import models.http.TaxableMigrationFlag
import models.{MigratingTrustDetails, NonMigratingTrustDetails, TrustDetailsType}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TrustsConnector @Inject()(http: HttpClient, config: AppConfig) {

  private val trustsUrl: String = s"${config.trustsUrl}/trusts"
  private val baseUrl: String = s"$trustsUrl/trust-details"

  def getTrustDetails(identifier: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[TrustDetailsType] = {
    val url = s"$baseUrl/$identifier/transformed"
    http.GET[TrustDetailsType](url)
  }

  def setUkProperty(identifier: String, value: Boolean)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    val url = s"$baseUrl/$identifier/uk-property"
    http.PUT[Boolean, HttpResponse](url, value)
  }

  def setTrustRecorded(identifier: String, value: Boolean)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    val url = s"$baseUrl/$identifier/recorded"
    http.PUT[Boolean, HttpResponse](url, value)
  }

  def setUkRelation(identifier: String, value: Boolean)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    val url = s"$baseUrl/$identifier/uk-relation"
    http.PUT[Boolean, HttpResponse](url, value)
  }

  def setUkResident(identifier: String, value: Boolean)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    val url = s"$baseUrl/$identifier/uk-resident"
    http.PUT[Boolean, HttpResponse](url, value)
  }

  def getTrustMigrationFlag(identifier: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[TaxableMigrationFlag] = {
    val url = s"$trustsUrl/$identifier/taxable-migration/migrating-to-taxable"
    http.GET[TaxableMigrationFlag](url)
  }

  def setMigratingTrustDetails(identifier: String, value: MigratingTrustDetails)
                              (implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    val url = s"$baseUrl/$identifier/migrating-trust-details"
    http.PUT[MigratingTrustDetails, HttpResponse](url, value)
  }

  def setNonMigratingTrustDetails(identifier: String, value: NonMigratingTrustDetails)
                                 (implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    val url = s"$baseUrl/$identifier/non-migrating-trust-details"
    http.PUT[NonMigratingTrustDetails, HttpResponse](url, value)
  }

  def wasTrustRegisteredWithDeceasedSettlor(identifier: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[Boolean] = {
    val url = s"$baseUrl/$identifier/has-deceased-settlor"
    http.GET[Boolean](url)
  }

  def getTrustName(identifier: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[String] = {
    val url = s"$baseUrl/$identifier/trust-name"
    http.GET[String](url)
  }

  def removeTrustTypeDependentTransformFields(identifier: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    val url = s"$trustsUrl/$identifier/trust-type-dependent-transform-fields"
    http.DELETE[HttpResponse](url)
  }

  def removeOptionalTrustDetailTransforms(identifier: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    val url = s"$baseUrl/$identifier/optional-trust-detail-transforms"
    http.DELETE[HttpResponse](url)
  }

}
