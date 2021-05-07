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

package pages.maintain

import models.DeedOfVariation.ReplacedWillTrust
import models.TypeOfTrust
import models.TypeOfTrust._
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

import java.time.LocalDate

class TypeOfTrustPageSpec extends PageBehaviours {

  "TypeOfTrustPage" must {

    beRetrievable[TypeOfTrust](TypeOfTrustPage)

    beSettable[TypeOfTrust](TypeOfTrustPage)

    beRemovable[TypeOfTrust](TypeOfTrustPage)

    "implement cleanup logic" when {
      "something other than InterVivosSettlement selected" in {

        forAll(arbitrary[TypeOfTrust].suchThat(_ != InterVivosSettlement)) {
          typeOfTrust =>

            val userAnswers = emptyUserAnswers
              .set(HoldoverReliefClaimedPage, true).success.value

            val cleanAnswers = userAnswers.set(TypeOfTrustPage, typeOfTrust).success.value

            cleanAnswers.get(HoldoverReliefClaimedPage) mustBe None
        }
      }

      "something other than EmploymentRelated selected" in {

        forAll(arbitrary[TypeOfTrust].suchThat(_ != EmploymentRelated)) {
          typeOfTrust =>

            val userAnswers = emptyUserAnswers
              .set(EfrbsYesNoPage, true).success.value
              .set(EfrbsStartDatePage, LocalDate.parse("1996-02-03")).success.value

            val cleanAnswers = userAnswers.set(TypeOfTrustPage, typeOfTrust).success.value

            cleanAnswers.get(EfrbsYesNoPage) mustBe None
            cleanAnswers.get(EfrbsStartDatePage) mustBe None
        }
      }

      "something other than DeedOfVariationTrustOrFamilyArrangement selected" in {

        forAll(arbitrary[TypeOfTrust].suchThat(_ != DeedOfVariationTrustOrFamilyArrangement)) {
          typeOfTrust =>

            val userAnswers = emptyUserAnswers
              .set(SetUpInAdditionToWillTrustPage, false).success.value
              .set(WhyDeedOfVariationCreatedPage, ReplacedWillTrust).success.value

            val cleanAnswers = userAnswers.set(TypeOfTrustPage, typeOfTrust).success.value

            cleanAnswers.get(SetUpInAdditionToWillTrustPage) mustBe None
            cleanAnswers.get(WhyDeedOfVariationCreatedPage) mustBe None
        }
      }
    }
  }
}
