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

package models

import play.api.libs.json._

import java.time.LocalDate

final case class TrustDetailsType(startDate: LocalDate,
                                  lawCountry: Option[String],
                                  administrationCountry: Option[String],
                                  residentialStatus: Option[ResidentialStatusType],
                                  trustUKProperty: Option[Boolean],
                                  trustRecorded: Option[Boolean],
                                  trustUKRelation: Option[Boolean],
                                  trustUKResident: Option[Boolean],
                                  typeOfTrust: Option[TypeOfTrust],
                                  deedOfVariation: Option[DeedOfVariation],
                                  interVivos: Option[Boolean],
                                  efrbsStartDate: Option[LocalDate]) {

  def ukResident: Boolean = (residentialStatus, trustUKResident) match {
    case (Some(ResidentialStatusType(Some(_), None)), _) => true
    case (_, Some(true)) => true
    case _ => false
  }
}

object TrustDetailsType {
  implicit val trustDetailsTypeFormat: Format[TrustDetailsType] = Json.format[TrustDetailsType]
}

case class ResidentialStatusType(uk: Option[UkType] = None,
                                 nonUK: Option[NonUKType] = None)

object ResidentialStatusType {
  implicit val residentialStatusTypeFormat: Format[ResidentialStatusType] = Json.format[ResidentialStatusType]
}

case class UkType(scottishLaw: Boolean,
                  preOffShore: Option[String])

object UkType {
  implicit val ukTypeFormat: Format[UkType] = Json.format[UkType]
}

case class NonUKType(sch5atcgga92: Boolean,
                     s218ihta84: Option[Boolean],
                     agentS218IHTA84: Option[Boolean],
                     trusteeStatus: Option[String] = None)

object NonUKType {
  implicit val nonUKTypeFormat: Format[NonUKType] = Json.format[NonUKType]
}

sealed trait TrustDetails

/**
 * Used for mapping answers when maintaining trust details in taxable and non-taxable
 */
case class NonMigratingTrustDetails(trustUKProperty: Boolean,
                                    trustRecorded: Boolean,
                                    trustUKRelation: Option[Boolean],
                                    trustUKResident: Boolean) extends TrustDetails

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
                                 residentialStatus: ResidentialStatusType,
                                 trustUKProperty: Boolean,
                                 trustRecorded: Boolean,
                                 trustUKRelation: Option[Boolean],
                                 trustUKResident: Boolean,
                                 typeOfTrust: TypeOfTrust,
                                 deedOfVariation: Option[DeedOfVariation],
                                 interVivos: Option[Boolean],
                                 efrbsStartDate: Option[LocalDate],
                                 settlorsUkBased: Option[Boolean] = None) extends TrustDetails

object MigratingTrustDetails {
  implicit val format: Format[MigratingTrustDetails] = Json.format[MigratingTrustDetails]
}
