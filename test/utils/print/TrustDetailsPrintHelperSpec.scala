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

package utils.print

import base.SpecBase
import controllers.maintain.routes._
import models.DeedOfVariation.ReplacedWillTrust
import models.TrusteesBased.AllTrusteesUkBased
import models.TypeOfTrust.DeedOfVariationTrustOrFamilyArrangement
import pages.maintain._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

import java.time.LocalDate

class TrustDetailsPrintHelperSpec extends SpecBase {

  private val printHelper = injector.instanceOf[TrustDetailsPrintHelper]

  "TrustDetailsPrintHelper" must {

    "render answer rows" when {

      "migrating from non-taxable to taxable" in {

        val trustName = "Trust Name"

        val userAnswers = emptyUserAnswers.copy(migratingFromNonTaxableToTaxable = true)
          .set(NamePage, trustName).success.value
          .set(StartDatePage, LocalDate.parse("2000-01-01")).success.value
          .set(GovernedByUkLawPage, false).success.value
          .set(GoverningCountryPage, "DE").success.value
          .set(AdministeredInUkPage, false).success.value
          .set(AdministrationCountryPage, "FR").success.value
          .set(SetUpAfterSettlorDiedPage, false).success.value
          .set(TypeOfTrustPage, DeedOfVariationTrustOrFamilyArrangement).success.value
          .set(WhyDeedOfVariationCreatedPage, ReplacedWillTrust).success.value
          .set(HoldoverReliefClaimedPage, true).success.value
          .set(EfrbsYesNoPage, true).success.value
          .set(EfrbsStartDatePage, LocalDate.parse("1996-02-03")).success.value
          .set(OwnsUkLandOrPropertyPage, true).success.value
          .set(RecordedOnEeaRegisterPage, true).success.value
          .set(WhereTrusteesBasedPage, AllTrusteesUkBased).success.value
          .set(SettlorsUkBasedPage, true).success.value
          .set(CreatedUnderScotsLawPage, true).success.value
          .set(PreviouslyResidentOffshorePage, true).success.value
          .set(PreviouslyResidentOffshoreCountryPage, "US").success.value
          .set(BusinessRelationshipInUkPage, true).success.value
          .set(SettlorBenefitsFromAssetsPage, false).success.value
          .set(ForPurposeOfSection218Page, true).success.value
          .set(AgentCreatedTrustPage, true).success.value

        val result = printHelper(userAnswers)

        result mustEqual AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(messages("name.checkYourAnswersLabel"), Html(trustName), canEdit = false, changeUrl = None),
            AnswerRow(messages("startDate.checkYourAnswersLabel"), Html("1 January 2000"), canEdit = false, changeUrl = None),
            AnswerRow(messages("governedByUkLawYesNo.checkYourAnswersLabel"), Html("No"), Some(GovernedByUkLawController.onPageLoad().url)),
            AnswerRow(messages("governingCountry.checkYourAnswersLabel"), Html("Germany"), Some(GoverningCountryController.onPageLoad().url)),
            AnswerRow(messages("administeredInUkYesNo.checkYourAnswersLabel"), Html("No"), Some(AdministeredInUkController.onPageLoad().url)),
            AnswerRow(messages("administrationCountry.checkYourAnswersLabel"), Html("France"), Some(AdministrationCountryController.onPageLoad().url)),
            AnswerRow(messages("setUpAfterSettlorDiedYesNo.checkYourAnswersLabel"), Html("No"), Some(SetUpAfterSettlorDiedController.onPageLoad().url)),
            AnswerRow(messages("typeOfTrust.checkYourAnswersLabel"), Html("A trust through a Deed of Variation or family agreement"), Some(TypeOfTrustController.onPageLoad().url)),
            AnswerRow(messages("whyDeedOfVariationCreated.checkYourAnswersLabel"), Html("To replace a will trust"), Some(WhyDeedOfVariationCreatedController.onPageLoad().url)),
            AnswerRow(messages("holdoverReliefClaimedYesNo.checkYourAnswersLabel"), Html("Yes"), Some(HoldoverReliefClaimedController.onPageLoad().url)),
            AnswerRow(messages("efrbsYesNo.checkYourAnswersLabel"), Html("Yes"), Some(EfrbsYesNoController.onPageLoad().url)),
            AnswerRow(messages("efrbsStartDate.checkYourAnswersLabel"), Html("3 February 1996"), Some(EfrbsStartDateController.onPageLoad().url)),
            AnswerRow(messages("ownsUkLandOrPropertyYesNo.checkYourAnswersLabel"), Html("Yes"), Some(OwnsUkLandOrPropertyController.onPageLoad().url)),
            AnswerRow(messages("recordedOnEeaRegisterYesNo.checkYourAnswersLabel"), Html("Yes"), Some(RecordedOnEeaRegisterController.onPageLoad().url)),
            AnswerRow(messages("whereTrusteesBased.checkYourAnswersLabel"), Html("All the trustees are based in the UK"), Some(WhereTrusteesBasedController.onPageLoad().url)),
            AnswerRow(messages("settlorsUkBasedYesNo.checkYourAnswersLabel"), Html("Yes"), Some(SettlorsUkBasedController.onPageLoad().url)),
            AnswerRow(messages("createdUnderScotsLawYesNo.checkYourAnswersLabel"), Html("Yes"), Some(CreatedUnderScotsLawController.onPageLoad().url)),
            AnswerRow(messages("previouslyResidentOffshoreYesNo.checkYourAnswersLabel"), Html("Yes"), Some(PreviouslyResidentOffshoreController.onPageLoad().url)),
            AnswerRow(messages("previouslyResidentOffshoreCountry.checkYourAnswersLabel"), Html("United States of America"), Some(PreviouslyResidentOffshoreCountryController.onPageLoad().url)),
            AnswerRow(messages("businessRelationshipInUkYesNo.checkYourAnswersLabel"), Html("Yes"), Some(BusinessRelationshipInUkController.onPageLoad().url)),
            AnswerRow(messages("settlorBenefitsFromAssetsYesNo.checkYourAnswersLabel"), Html("No"), Some(SettlorBenefitsFromAssetsController.onPageLoad().url)),
            AnswerRow(messages("forPurposeOfSection218YesNo.checkYourAnswersLabel"), Html("Yes"), Some(ForPurposeOfSection218Controller.onPageLoad().url)),
            AnswerRow(messages("agentCreatedTrustYesNo.checkYourAnswersLabel"), Html("Yes"), Some(AgentCreatedTrustController.onPageLoad().url))
          ),
          sectionKey = None
        )
      }

      "not migrating" in {
        val userAnswers = emptyUserAnswers.copy(migratingFromNonTaxableToTaxable = false)
          .set(OwnsUkLandOrPropertyPage, true).success.value
          .set(RecordedOnEeaRegisterPage, false).success.value
          .set(BusinessRelationshipInUkPage, true).success.value

        val result = printHelper(userAnswers)

        result mustEqual AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(messages("ownsUkLandOrPropertyYesNo.checkYourAnswersLabel"), Html("Yes"), Some(OwnsUkLandOrPropertyController.onPageLoad().url)),
            AnswerRow(messages("recordedOnEeaRegisterYesNo.checkYourAnswersLabel"), Html("No"), Some(RecordedOnEeaRegisterController.onPageLoad().url)),
            AnswerRow(messages("businessRelationshipInUkYesNo.checkYourAnswersLabel"), Html("Yes"), Some(BusinessRelationshipInUkController.onPageLoad().url))
          ),
          sectionKey = None
        )
      }
    }
  }

}
