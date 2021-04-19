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

import models.{TrustDetailsType, UserAnswers}
import pages._

import scala.util.Try

class TrustDetailsExtractor {

  def apply(answers: UserAnswers, trustDetails: TrustDetailsType): Try[UserAnswers] =
    answers.deleteAtPath(pages.basePath)
      .flatMap(_.set(TrustOwnUKLandOrPropertyPage, trustDetails.trustUKProperty))
      .flatMap(_.set(TrustEEAYesNoPage, trustDetails.trustRecorded))
      .flatMap(_.set(TrustUKResidentPage, trustDetails.trustUKResident))
      .flatMap(_.set(BusinessRelationshipYesNoPage, trustDetails.trustUKRelation))

}
