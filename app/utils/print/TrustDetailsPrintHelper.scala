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

package utils.print

import com.google.inject.Inject
import controllers.maintain.routes._
import models.UserAnswers
import pages.maintain._
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

class TrustDetailsPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) {

  def apply(userAnswers: UserAnswers)(implicit messages: Messages): AnswerSection = {

    val bound = answerRowConverter.bind(userAnswers)

    val answerRows: Seq[Option[AnswerRow]] = if (userAnswers.migratingFromNonTaxableToTaxable) {
      Seq(
        bound.yesNoQuestion(GovernedByUkLawPage, "governedByUkLaw", GovernedByUkLawController.onPageLoad().url),
        bound.countryQuestion(GoverningCountryPage, "governingCountry", GoverningCountryController.onPageLoad().url),
        bound.yesNoQuestion(AdministeredInUkPage, "administeredInUk", AdministeredInUkController.onPageLoad().url),
        bound.countryQuestion(AdministrationCountryPage, "administrationCountry", AdministrationCountryController.onPageLoad().url),
        bound.yesNoQuestion(SetUpAfterSettlorDiedPage, "setUpAfterSettlorDied", SetUpAfterSettlorDiedController.onPageLoad().url),
        bound.enumQuestion(TypeOfTrustPage, "typeOfTrust", TypeOfTrustController.onPageLoad().url),
        bound.enumQuestion(WhyDeedOfVariationCreatedPage, "whyDeedOfVariationCreated", WhyDeedOfVariationCreatedController.onPageLoad().url),
        bound.yesNoQuestion(HoldoverReliefClaimedPage, "holdoverReliefClaimed", HoldoverReliefClaimedController.onPageLoad().url),
        bound.yesNoQuestion(EfrbsYesNoPage, "efrbsYesNo", EfrbsYesNoController.onPageLoad().url),
        bound.dateQuestion(EfrbsStartDatePage, "efrbsStartDate", EfrbsStartDateController.onPageLoad().url),
        bound.yesNoQuestion(OwnsUkLandOrPropertyPage, "ownsUkLandOrProperty", OwnsUkLandOrPropertyController.onPageLoad().url),
        bound.yesNoQuestion(RecordedOnEeaRegisterPage, "recordedOnEeaRegister", RecordedOnEeaRegisterController.onPageLoad().url),
        bound.enumQuestion(WhereTrusteesBasedPage, "whereTrusteesBased", WhereTrusteesBasedController.onPageLoad().url),
        //bound.yesNoQuestion(SettlorsUkBasedPage, "settlorsUkBased", SettlorsUkBasedController.onPageLoad().url),
        //bound.yesNoQuestion(CreatedUnderScotsLawPage, "createdUnderScotsLaw", CreatedUnderScotsLawController.onPageLoad().url),
        //bound.yesNoQuestion(PreviouslyResidentOffshorePage, "previouslyResidentOffshore", PreviouslyResidentOffshoreController.onPageLoad().url),
        //bound.countryQuestion(PreviouslyResidentOffshoreCountryPage, "previouslyResidentOffshoreCountry", PreviouslyResidentOffshoreCountryController.onPageLoad().url),
        bound.yesNoQuestion(BusinessRelationshipInUkPage, "businessRelationshipInUk", BusinessRelationshipInUkController.onPageLoad().url)
        //bound.yesNoQuestion(SettlorBenefitsFromAssetsPage, "settlorBenefitsFromAssets", SettlorBenefitsFromAssetsController.onPageLoad().url),
        //bound.yesNoQuestion(ForPurposeOfSection218Page, "forPurposeOfSection218", ForPurposeOfSection218Controller.onPageLoad().url),
        //bound.yesNoQuestion(AgentCreatedTrustPage, "agentCreatedTrust", AgentCreatedTrustController.onPageLoad().url),
      )
    } else {
      Seq(
        bound.yesNoQuestion(OwnsUkLandOrPropertyPage, "ownsUkLandOrProperty", OwnsUkLandOrPropertyController.onPageLoad().url),
        bound.yesNoQuestion(RecordedOnEeaRegisterPage, "recordedOnEeaRegister", RecordedOnEeaRegisterController.onPageLoad().url),
        bound.yesNoQuestion(BusinessRelationshipInUkPage, "businessRelationshipInUk", BusinessRelationshipInUkController.onPageLoad().url)
      )
    }

    AnswerSection(None, answerRows.flatten)

  }
}
