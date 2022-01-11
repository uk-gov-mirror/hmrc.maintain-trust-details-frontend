/*
 * Copyright 2022 HM Revenue & Customs
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

class SettlorBenefitsFromAssetsPageSpec extends PageBehaviours {

  "SettlorBenefitsFromAssetsPage" must {

    beRetrievable[Boolean](SettlorBenefitsFromAssetsPage)

    beSettable[Boolean](SettlorBenefitsFromAssetsPage)

    beRemovable[Boolean](SettlorBenefitsFromAssetsPage)

    "implement cleanup logic" when {

      "YES selected" in {

        val userAnswers = emptyUserAnswers
          .set(SettlorBenefitsFromAssetsPage, false).success.value
          .set(ForPurposeOfSection218Page, true).success.value
          .set(AgentCreatedTrustPage, true).success.value

        val cleanAnswers = userAnswers.set(SettlorBenefitsFromAssetsPage, true).success.value

        cleanAnswers.get(ForPurposeOfSection218Page) mustBe None
        cleanAnswers.get(AgentCreatedTrustPage) mustBe None
      }
    }
  }
}
