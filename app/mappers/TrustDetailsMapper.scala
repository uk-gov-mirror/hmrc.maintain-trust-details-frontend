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

import models.UserAnswers
import pages.maintain._
import play.api.Logging
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsResult, Reads}

class TrustDetailsMapper extends Logging {

  def apply(userAnswers: UserAnswers): JsResult[MappedTrustDetails] = {

    val reads: Reads[MappedTrustDetails] = (
      OwnsUkLandOrPropertyPage.path.read[Boolean] and
        RecordedOnEeaRegisterPage.path.read[Boolean] and
        BusinessRelationshipInUkPage.path.readNullable[Boolean] and
        TrustResidentInUkPage.path.read[Boolean]
      )(MappedTrustDetails.apply _)

    userAnswers.data.validate[MappedTrustDetails](reads)
  }

}

/**
 * Used for mapping answers when maintaining trust details in taxable and non-taxable
 */
case class MappedTrustDetails(trustUKProperty: Boolean,
                              trustRecorded: Boolean,
                              trustUKRelation: Option[Boolean],
                              trustUKResident: Boolean)
