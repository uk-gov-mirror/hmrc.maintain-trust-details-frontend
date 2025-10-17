/*
 * Copyright 2025 HM Revenue & Customs
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
        bound.stringQuestion(NamePage, "name", None, canEdit = false),
        bound.dateQuestion(StartDatePage, "startDate", None, canEdit = false),
        bound.yesNoQuestion(GovernedByUkLawPage, "governedByUkLawYesNo", Some(GovernedByUkLawController.onPageLoad().url)),
        bound.countryQuestion(GoverningCountryPage, "governingCountry", Some(GoverningCountryController.onPageLoad().url)),
        bound.yesNoQuestion(AdministeredInUkPage, "administeredInUkYesNo", Some(AdministeredInUkController.onPageLoad().url)),
        bound.countryQuestion(AdministrationCountryPage, "administrationCountry", Some(AdministrationCountryController.onPageLoad().url)),
        bound.yesNoQuestion(SetUpAfterSettlorDiedPage, "setUpAfterSettlorDiedYesNo", Some(SetUpAfterSettlorDiedController.onPageLoad().url)),
        bound.enumQuestion(TypeOfTrustPage, "typeOfTrust", Some(TypeOfTrustController.onPageLoad().url)),
        bound.enumQuestion(WhyDeedOfVariationCreatedPage, "whyDeedOfVariationCreated", Some(WhyDeedOfVariationCreatedController.onPageLoad().url)),
        bound.yesNoQuestion(HoldoverReliefClaimedPage, "holdoverReliefClaimedYesNo", Some(HoldoverReliefClaimedController.onPageLoad().url)),
        bound.yesNoQuestion(EfrbsYesNoPage, "efrbsYesNo", Some(EfrbsYesNoController.onPageLoad().url)),
        bound.dateQuestion(EfrbsStartDatePage, "efrbsStartDate", Some(EfrbsStartDateController.onPageLoad().url)),
        bound.yesNoQuestion(OwnsUkLandOrPropertyPage, "ownsUkLandOrPropertyYesNo", Some(OwnsUkLandOrPropertyController.onPageLoad().url)),
        bound.yesNoQuestion(RecordedOnEeaRegisterPage, "recordedOnEeaRegisterYesNo", Some(RecordedOnEeaRegisterController.onPageLoad().url)),
        bound.enumQuestion(WhereTrusteesBasedPage, "whereTrusteesBased", Some(WhereTrusteesBasedController.onPageLoad().url)),
        bound.yesNoQuestion(SettlorsUkBasedPage, "settlorsUkBasedYesNo", Some(SettlorsUkBasedController.onPageLoad().url)),
        bound.yesNoQuestion(CreatedUnderScotsLawPage, "createdUnderScotsLawYesNo", Some(CreatedUnderScotsLawController.onPageLoad().url)),
        bound.yesNoQuestion(PreviouslyResidentOffshorePage, "previouslyResidentOffshoreYesNo", Some(PreviouslyResidentOffshoreController.onPageLoad().url)),
        bound.countryQuestion(PreviouslyResidentOffshoreCountryPage, "previouslyResidentOffshoreCountry", Some(PreviouslyResidentOffshoreCountryController.onPageLoad().url)),
        bound.yesNoQuestion(BusinessRelationshipInUkPage, "businessRelationshipInUkYesNo", Some(BusinessRelationshipInUkController.onPageLoad().url)),
        bound.yesNoQuestion(SettlorBenefitsFromAssetsPage, "settlorBenefitsFromAssetsYesNo", Some(SettlorBenefitsFromAssetsController.onPageLoad().url)),
        bound.yesNoQuestion(ForPurposeOfSection218Page, "forPurposeOfSection218YesNo", Some(ForPurposeOfSection218Controller.onPageLoad().url)),
        bound.yesNoQuestion(AgentCreatedTrustPage, "agentCreatedTrustYesNo", Some(AgentCreatedTrustController.onPageLoad().url)),
        bound.yesNoQuestion(Schedule3aExemptYesNoPage, "schedule3aExemptYesNo", Some(Schedule3aExemptYesNoController.onPageLoad().url))
      )
    } else {
      Seq(
        bound.yesNoQuestion(OwnsUkLandOrPropertyPage, "ownsUkLandOrPropertyYesNo", Some(OwnsUkLandOrPropertyController.onPageLoad().url)),
        bound.yesNoQuestion(RecordedOnEeaRegisterPage, "recordedOnEeaRegisterYesNo", Some(RecordedOnEeaRegisterController.onPageLoad().url)),
        bound.yesNoQuestion(BusinessRelationshipInUkPage, "businessRelationshipInUkYesNo", Some(BusinessRelationshipInUkController.onPageLoad().url)),
        bound.yesNoQuestion(Schedule3aExemptYesNoPage, "schedule3aExemptYesNo", Some(Schedule3aExemptYesNoController.onPageLoad().url))
      )
    }

    AnswerSection(None, answerRows.flatten)

  }
}
