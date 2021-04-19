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

package extractors

import java.time.LocalDate

import com.google.inject.Inject
import models.TrusteesBasedInTheUK.{InternationalAndUKTrustees, NonUkBasedTrustees, UKBasedTrustees}
import models.{NonUKType, ResidentialStatusType, TrustDetailsType, TrusteesBasedInTheUK, UkType, UserAnswers}
import pages.{BusinessRelationshipYesNoPage, CountryAdministeringTrustPage, SettlorsBasedInTheUKPage, TrustEEAYesNoPage, TrustOwnUKLandOrPropertyPage, TrusteesBasedInTheUKPage, WhenTrustSetupPage}
import play.api.libs.json.{JsSuccess, Reads}

import scala.util.Try

class TrustDetailsExtractor @Inject()() {

  def apply(answers: UserAnswers, trustDetails: TrustDetailsType): Try[UserAnswers] =
    answers.deleteAtPath(pages.basePath)
      .flatMap(_.set(WhenTrustSetupPage, trustDetails.startDate))
      .flatMap(_.set(CountryAdministeringTrustPage, trustDetails.administrationCountry))
      .flatMap(answers => extractResidentialStatus(trustDetails.residentialStatus, answers))
      .flatMap(_.set(TrustOwnUKLandOrPropertyPage, trustDetails.trustUKProperty))
      .flatMap(_.set(TrustEEAYesNoPage, trustDetails.trustRecorded))
      .flatMap(_.set(BusinessRelationshipYesNoPage, trustDetails.trustRecorded))
      .flatMap(_.set(trustUkResidentReads(answers), trustDetails.trustUKResident))


  private def extractResidentialStatus(residentialStatus: Option[ResidentialStatusType], answers: UserAnswers): Try[UserAnswers] = {
    residentialStatus match {
      case Some(uk: UkType) =>
        answers.set(scottishLaw, true)
          .flatMap(_.set(preOffShore, ""))
      case Some(nonUk: NonUKType) =>
        answers.set(sch5atcgga92, true)
          .flatMap(_.set(s218ihta84, true))
          .flatMap(_.set(agentS218IHTA84, true))
          .flatMap(_.set(trusteeStatus, ""))
    }
  }

  private def trustUkResidentReads(ua: UserAnswers): Reads[Option[Boolean]] = {
    basedInUkReads[Boolean](Reads(_ => JsSuccess(Some(true))), Reads(_ => JsSuccess(Some(false))))
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
