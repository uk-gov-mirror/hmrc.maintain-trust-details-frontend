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

import models.DeedOfVariation._
import models.TypeOfTrust._
import models.{ResidentialStatusType, TrustDetailsType, UserAnswers}
import pages.maintain._

import java.time.LocalDate
import scala.util.{Failure, Success, Try}

class TrustDetailsExtractor {

  def apply(answers: UserAnswers, trustDetails: TrustDetailsType): Try[UserAnswers] =
    answers.deleteAtPath(pages.maintain.basePath)
      .flatMap(_.set(OwnsUkLandOrPropertyPage, trustDetails.trustUKProperty))
      .flatMap(_.set(RecordedOnEeaRegisterPage, trustDetails.trustRecorded))
      .flatMap(ua => extractTrustType(trustDetails, ua))
      .flatMap(ua => extractTrustUKResident(trustDetails, ua))
      .flatMap(_.set(BusinessRelationshipInUkPage, trustDetails.trustUKRelation))

  private def extractTrustType(trustDetails: TrustDetailsType, answers: UserAnswers): Try[UserAnswers] = {
    (trustDetails.typeOfTrust, trustDetails.deedOfVariation) match {
      case (Some(WillTrustOrIntestacyTrust), None) => answers
        .set(SetUpAfterSettlorDiedPage, true)
      case (Some(WillTrustOrIntestacyTrust), Some(AdditionToWillTrust)) => answers
        .set(SetUpAfterSettlorDiedPage, false)
        .flatMap(_.set(TypeOfTrustPage, DeedOfVariationTrustOrFamilyArrangement))
        .flatMap(_.set(SetUpInAdditionToWillTrustPage, true))
      case (Some(DeedOfVariationTrustOrFamilyArrangement), _) => answers
        .set(SetUpAfterSettlorDiedPage, false)
        .flatMap(_.set(TypeOfTrustPage, DeedOfVariationTrustOrFamilyArrangement))
        .flatMap(_.set(SetUpInAdditionToWillTrustPage, false))
        .flatMap(_.set(WhyDeedOfVariationCreatedPage, trustDetails.deedOfVariation))
      case (Some(InterVivosSettlement), _) => answers
        .set(SetUpAfterSettlorDiedPage, false)
        .flatMap(_.set(TypeOfTrustPage, InterVivosSettlement))
        .flatMap(_.set(HoldoverReliefClaimedPage, trustDetails.interVivos))
      case (Some(EmploymentRelated), _) => answers
        .set(SetUpAfterSettlorDiedPage, false)
        .flatMap(_.set(TypeOfTrustPage, EmploymentRelated))
        .flatMap(ua => extractEfrbs(trustDetails.efrbsStartDate, ua))
      case (Some(HeritageMaintenanceFund), _) => answers
        .set(SetUpAfterSettlorDiedPage, false)
        .flatMap(_.set(TypeOfTrustPage, HeritageMaintenanceFund))
      case (Some(FlatManagementCompanyOrSinkingFund), _) => answers
        .set(SetUpAfterSettlorDiedPage, false)
        .flatMap(_.set(TypeOfTrustPage, FlatManagementCompanyOrSinkingFund))
      case _ =>
        Success(answers)
    }
  }

  private def extractEfrbs(efrbsStartDate: Option[LocalDate], answers: UserAnswers): Try[UserAnswers] = {
    efrbsStartDate match {
      case Some(value) => answers
        .set(EfrbsYesNoPage, true)
        .flatMap(_.set(EfrbsStartDatePage, value))
      case None => answers
        .set(EfrbsYesNoPage, false)
    }
  }

  private def extractTrustUKResident(trustDetails: TrustDetailsType, answers: UserAnswers): Try[UserAnswers] = {
    (trustDetails.trustUKResident, trustDetails.residentialStatus) match {
      case (Some(value), _) => answers.set(TrustResidentInUkPage, value)
      case (_, Some(ResidentialStatusType(Some(_), None))) => answers.set(TrustResidentInUkPage, true)
      case (_, Some(ResidentialStatusType(None, Some(_)))) => answers.set(TrustResidentInUkPage, false)
      case _ => Failure(new Throwable("Trust details in unexpected shape"))
    }
  }
}
