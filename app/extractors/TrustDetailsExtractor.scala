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

import models.{ResidentialStatusType, TrustDetailsType, UserAnswers}
import pages._

import scala.util.{Failure, Try}

class TrustDetailsExtractor {

  def apply(answers: UserAnswers, trustDetails: TrustDetailsType): Try[UserAnswers] =
    answers.deleteAtPath(pages.basePath)
      .flatMap(_.set(TrustOwnUKLandOrPropertyPage, trustDetails.trustUKProperty))
      .flatMap(_.set(TrustEEAYesNoPage, trustDetails.trustRecorded))
      .flatMap(answers => extractTrustUKResident(trustDetails, answers))
      .flatMap(_.set(BusinessRelationshipYesNoPage, trustDetails.trustUKRelation))


  private def extractTrustUKResident(trustDetails: TrustDetailsType, answers: UserAnswers): Try[UserAnswers] = {
    (trustDetails.trustUKResident, trustDetails.residentialStatus) match {
      case (Some(value), _) => answers.set(TrustUKResidentPage, value)
      case (_, Some(ResidentialStatusType(Some(_), None))) => answers.set(TrustUKResidentPage, true)
      case (_, Some(ResidentialStatusType(None, Some(_)))) => answers.set(TrustUKResidentPage, false)
      case _ => Failure(new Throwable("Trust Details in unexpected shape"))
    }
  }
}
