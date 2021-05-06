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

package mappers

import models.{DeedOfVariation, ResidentialStatusType, TypeOfTrust, UserAnswers}
import pages.maintain._
import play.api.Logging
import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsResult, Json, Reads}

import java.time.LocalDate

class TrustDetailsMapper extends Logging {

  def apply(userAnswers: UserAnswers): JsResult[NonMigratingTrustDetails] = {

    val reads: Reads[NonMigratingTrustDetails] = (
      OwnsUkLandOrPropertyPage.path.read[Boolean] and
        RecordedOnEeaRegisterPage.path.read[Boolean] and
        BusinessRelationshipInUkPage.path.readNullable[Boolean] and
        TrustResidentInUkPage.path.read[Boolean]
      )(NonMigratingTrustDetails.apply _)

    userAnswers.data.validate[NonMigratingTrustDetails](reads)
  }

}

/**
 * Used for mapping answers when maintaining trust details in taxable and non-taxable
 */
case class NonMigratingTrustDetails(trustUKProperty: Boolean,
                                    trustRecorded: Boolean,
                                    trustUKRelation: Option[Boolean],
                                    trustUKResident: Boolean)

object NonMigratingTrustDetails {
  implicit val format: Format[NonMigratingTrustDetails] = Json.format[NonMigratingTrustDetails]
}

/**
 * Used for mapping answers when migrating from non-taxable to taxable
 * @param lawCountry - either Some(nonUkCountry) or None
 * @param administrationCountry - either nonUkCountry or GB
 * @param trustUKResident - driven by whether residentialStatus contains UkType or NonUKType
 */
case class MigratingTrustDetails(lawCountry: Option[String],
                                 administrationCountry: String,
                                 residentialStatus: Option[ResidentialStatusType],
                                 trustUKRelation: Option[Boolean],
                                 trustUKResident: Boolean,
                                 typeOfTrust: TypeOfTrust,
                                 deedOfVariation: Option[DeedOfVariation],
                                 interVivos: Option[Boolean],
                                 efrbsStartDate: Option[LocalDate])

object MigratingTrustDetails {
  implicit val format: Format[MigratingTrustDetails] = Json.format[MigratingTrustDetails]
}
