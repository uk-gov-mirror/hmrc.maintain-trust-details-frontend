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

package mapping

import java.time.LocalDate

import models.TrusteesBasedInTheUK.{InternationalAndUKTrustees, NonUkBasedTrustees, UKBasedTrustees}
import models.{NonUKType, ResidentialStatusType, TrustDetailsType, TrusteesBasedInTheUK, UkType, UserAnswers}
import pages.{BusinessRelationshipYesNoPage, CountryAdministeringTrustPage, CountryGoverningTrustPage, SettlorsBasedInTheUKPage, TrustEEAYesNoPage, TrustOwnUKLandOrPropertyPage, TrusteesBasedInTheUKPage, WhenTrustSetupPage}
import play.api.Logging
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsError, JsSuccess, Reads}

class TrustDetailsMapper extends Mapping[TrustDetailsType] with Logging {

  override def build(userAnswers: UserAnswers): Option[TrustDetailsType] = {

    def reads: Reads[TrustDetailsType] = (
      WhenTrustSetupPage.path.read[LocalDate] and
        CountryGoverningTrustPage.path.readNullable[String] and
        administrationCountryReads(userAnswers) and
        residentialStatusReads(userAnswers)and
        TrustOwnUKLandOrPropertyPage.path.readNullable[Boolean] and
        TrustEEAYesNoPage.path.readNullable[Boolean] and
        BusinessRelationshipYesNoPage.path.readNullable[Boolean] and
        trustUkResidentReads(userAnswers)
      )(TrustDetailsType.apply _)

    userAnswers.data.validate[TrustDetailsType](reads) match {
      case JsSuccess(value, _) =>
        Some(value)
      case JsError(errors) =>
        logger.error(s"Failed to rehydrate TrustDetailsType from UserAnswers due to $errors")
        None
    }
  }

  private def administrationCountryReads(ua: UserAnswers): Reads[Option[String]] = {
    if (ua.isTaxable) {
      CountryAdministeringTrustPage.path.read[String]
        .map(Some(_): Option[String])
        .orElse(Reads(_ => JsSuccess(Some(GB))))
    } else {
      Reads(_ => JsSuccess(None))
    }
  }

  private def trustUkResidentReads(ua: UserAnswers): Reads[Option[Boolean]] = {
    if (ua.is5mldEnabled) {
      basedInUkReads[Boolean](Reads(_ => JsSuccess(Some(true))), Reads(_ => JsSuccess(Some(false))))
    } else {
      Reads(_ => JsSuccess(None))
    }
  }

  private def residentialStatusReads(ua: UserAnswers): Reads[Option[ResidentialStatusType]] = {
    if (ua.isTaxable) {

      def combineReads(ukReads: Reads[Option[UkType]], nonUkReads: Reads[Option[NonUKType]]): Reads[Option[ResidentialStatusType]] = {
        (ukReads and nonUkReads)(ResidentialStatusType.apply _).map(Some(_))
      }

      lazy val ukReads: Reads[Option[ResidentialStatusType]] = {
        lazy val reads: Reads[Option[UkType]] =
          (EstablishedUnderScotsLawPage.path.read[Boolean] and
            TrustPreviouslyResidentPage.path.readNullable[String]
            )(UkType.apply _).map(Some(_))

        combineReads(reads, Reads(_ => JsSuccess(None: Option[NonUKType])))
      }

      lazy val nonUkReads: Reads[Option[ResidentialStatusType]] = {
        lazy val reads: Reads[Option[NonUKType]] =
          (RegisteringTrustFor5APage.path.read[Boolean] and
            InheritanceTaxActPage.path.readNullable[Boolean] and
            AgentOtherThanBarristerPage.path.readNullable[Boolean] and
            Reads(_ => JsSuccess(None))
            )(NonUKType.apply _).map(Some(_))

        combineReads(Reads(_ => JsSuccess(None: Option[UkType])), reads)
      }

      basedInUkReads[ResidentialStatusType](ukReads, nonUkReads)
    } else {
      Reads(_ => JsSuccess(None))
    }
  }

  private def basedInUkReads[T](ukReads: Reads[Option[T]], nonUkReads: Reads[Option[T]]): Reads[Option[T]] = {
    TrusteesBasedInTheUKPage.path.read[TrusteesBasedInTheUK].flatMap {
      case UKBasedTrustees => ukReads
      case NonUkBasedTrustees => nonUkReads
      case InternationalAndUKTrustees =>
        SettlorsBasedInTheUKPage.path.read[Boolean].flatMap {
          case true => ukReads
          case false => nonUkReads
        }
    }
  }

}
