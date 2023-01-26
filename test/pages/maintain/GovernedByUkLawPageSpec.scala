/*
 * Copyright 2023 HM Revenue & Customs
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

import pages.behaviours.PageBehaviours

class GovernedByUkLawPageSpec extends PageBehaviours {

  "GovernedByUkLawPage" must {

    beRetrievable[Boolean](GovernedByUkLawPage)

    beSettable[Boolean](GovernedByUkLawPage)

    beRemovable[Boolean](GovernedByUkLawPage)

    "implement cleanup logic" when {

      "YES selected" in {

        val userAnswers = emptyUserAnswers
          .set(GovernedByUkLawPage, false).success.value
          .set(GoverningCountryPage, "FR").success.value

        val cleanAnswers = userAnswers.set(GovernedByUkLawPage, true).success.value

        cleanAnswers.get(GoverningCountryPage) mustBe None
      }
    }
  }
}
