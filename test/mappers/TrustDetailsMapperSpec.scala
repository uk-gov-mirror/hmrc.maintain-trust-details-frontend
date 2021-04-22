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

import base.SpecBase
import pages.maintain._

class TrustDetailsMapperSpec extends SpecBase {

  private val mapper = injector.instanceOf[TrustDetailsMapper]

  "TrustDetailsMapper" must {

    "successfully map data" when {
      "TrustOwnUKLandOrPropertyPage and TrustEEAYesNoPage populated" when {

        "BusinessRelationshipYesNoPage populated" in {

          val userAnswers = emptyUserAnswers
            .set(TrustOwnUKLandOrPropertyPage, true).success.value
            .set(TrustEEAYesNoPage, true).success.value
            .set(BusinessRelationshipYesNoPage, true).success.value
            .set(TrustUKResidentPage, false).success.value

          val result = mapper(userAnswers)

          result mustBe Some(MappedTrustDetails(
            trustUKProperty = true,
            trustRecorded = true,
            trustUKRelation = Some(true),
            trustUKResident = false
          ))
        }

        "BusinessRelationshipYesNoPage not populated" in {

          val userAnswers = emptyUserAnswers
            .set(TrustOwnUKLandOrPropertyPage, true).success.value
            .set(TrustEEAYesNoPage, true).success.value
            .set(TrustUKResidentPage, true).success.value

          val result = mapper(userAnswers)

          result mustBe Some(MappedTrustDetails(
            trustUKProperty = true,
            trustRecorded = true,
            trustUKRelation = None,
            trustUKResident = true
          ))
        }
      }
    }

    "fail to map data" when {
      "TrustOwnUKLandOrPropertyPage, TrustEEAYesNoPage or TrustUKResidentPage not populated" when {

        "TrustOwnUKLandOrPropertyPage not populated" in {

          val userAnswers = emptyUserAnswers
            .set(TrustEEAYesNoPage, true).success.value
            .set(TrustUKResidentPage, false).success.value

          val result = mapper(userAnswers)

          result mustBe None
        }

        "TrustEEAYesNoPage not populated" in {

          val userAnswers = emptyUserAnswers
            .set(TrustOwnUKLandOrPropertyPage, true).success.value
            .set(TrustUKResidentPage, false).success.value

          val result = mapper(userAnswers)

          result mustBe None
        }

        "TrustUKResidentPage not populated" in {

          val userAnswers = emptyUserAnswers
            .set(TrustOwnUKLandOrPropertyPage, true).success.value
            .set(TrustEEAYesNoPage, true).success.value

          val result = mapper(userAnswers)

          result mustBe None
        }
      }
    }
  }
}
