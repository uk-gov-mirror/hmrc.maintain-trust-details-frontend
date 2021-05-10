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
import models.TypeOfTrust.DeedOfVariationTrustOrFamilyArrangement
import pages.behaviours.PageBehaviours

import java.time.LocalDate

class SetUpAfterSettlorDiedPageSpec extends PageBehaviours {

  "SetUpAfterSettlorDiedPage" must {

    beRetrievable[Boolean](SetUpAfterSettlorDiedPage)

    beSettable[Boolean](SetUpAfterSettlorDiedPage)

    beRemovable[Boolean](SetUpAfterSettlorDiedPage)

    "implement cleanup logic" when {
      "YES selected" in {

        val userAnswers = emptyUserAnswers
          .set(TypeOfTrustPage, DeedOfVariationTrustOrFamilyArrangement).success.value
          .set(WhyDeedOfVariationCreatedPage, ReplacedWillTrust).success.value
          .set(HoldoverReliefClaimedPage, true).success.value
          .set(EfrbsYesNoPage, true).success.value
          .set(EfrbsStartDatePage, LocalDate.parse("1996-02-03")).success.value

        val cleanAnswers = userAnswers.set(SetUpAfterSettlorDiedPage, true).success.value

        cleanAnswers.get(TypeOfTrustPage) mustBe None
        cleanAnswers.get(WhyDeedOfVariationCreatedPage) mustBe None
        cleanAnswers.get(HoldoverReliefClaimedPage) mustBe None
        cleanAnswers.get(EfrbsYesNoPage) mustBe None
        cleanAnswers.get(EfrbsStartDatePage) mustBe None
      }
    }
  }
}
