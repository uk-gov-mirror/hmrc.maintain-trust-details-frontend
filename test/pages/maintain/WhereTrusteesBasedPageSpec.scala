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

import models.TrusteesBased
import models.TrusteesBased._
import pages.behaviours.PageBehaviours

class WhereTrusteesBasedPageSpec extends PageBehaviours {

  "WhereTrusteesBasedPage" must {

    beRetrievable[TrusteesBased](WhereTrusteesBasedPage)

    beSettable[TrusteesBased](WhereTrusteesBasedPage)

    beRemovable[TrusteesBased](WhereTrusteesBasedPage)

    "implement cleanup logic" when {

      "AllTrusteesUkBased selected" in {

        val userAnswers = emptyUserAnswers
          .set(WhereTrusteesBasedPage, InternationalAndUkBasedTrustees).success.value
          .set(SettlorsUkBasedPage, false).success.value
          .set(BusinessRelationshipInUkPage, true).success.value
          .set(SettlorBenefitsFromAssetsPage, false).success.value
          .set(ForPurposeOfSection218Page, true).success.value
          .set(AgentCreatedTrustPage, true).success.value

        val cleanAnswers = userAnswers.set(WhereTrusteesBasedPage, AllTrusteesUkBased).success.value

        cleanAnswers.get(SettlorsUkBasedPage) mustBe None
        cleanAnswers.get(BusinessRelationshipInUkPage) mustBe None
        cleanAnswers.get(SettlorBenefitsFromAssetsPage) mustBe None
        cleanAnswers.get(ForPurposeOfSection218Page) mustBe None
        cleanAnswers.get(AgentCreatedTrustPage) mustBe None
      }

      "NoTrusteesUkBased selected" in {

        val userAnswers = emptyUserAnswers
          .set(WhereTrusteesBasedPage, InternationalAndUkBasedTrustees).success.value
          .set(SettlorsUkBasedPage, true).success.value
          .set(CreatedUnderScotsLawPage, true).success.value
          .set(PreviouslyResidentOffshorePage, true).success.value
          .set(PreviouslyResidentOffshoreCountryPage, "FR").success.value

        val cleanAnswers = userAnswers.set(WhereTrusteesBasedPage, NoTrusteesUkBased).success.value

        cleanAnswers.get(SettlorsUkBasedPage) mustBe None
        cleanAnswers.get(CreatedUnderScotsLawPage) mustBe None
        cleanAnswers.get(PreviouslyResidentOffshorePage) mustBe None
        cleanAnswers.get(PreviouslyResidentOffshoreCountryPage) mustBe None
      }
    }
  }
}
