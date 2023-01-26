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

package navigation

import config.AppConfig
import controllers.maintain.routes._
import controllers.routes.SessionExpiredController
import models.TrusteesBased._
import models.TypeOfTrust._
import models.UserAnswers
import pages.Page
import pages.maintain._
import play.api.mvc.Call
import javax.inject.Inject

class TrustDetailsNavigator @Inject()(appConfig: AppConfig) extends Navigator {

  override def nextPage(page: Page, userAnswers: UserAnswers): Call = {
    routes()(page)(userAnswers)
  }

  private def routes(): PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation() orElse
      conditionalNavigation()

  private def simpleNavigation(): PartialFunction[Page, UserAnswers => Call] = {
    case OwnsUkLandOrPropertyPage => _ => RecordedOnEeaRegisterController.onPageLoad()
    case BusinessRelationshipInUkPage => ua => navigateAwayFromBusinessRelationshipInUkQuestion(ua)
    case GoverningCountryPage => _ => AdministeredInUkController.onPageLoad()
    case Schedule3aExemptYesNoPage => _ => CheckDetailsController.onPageLoad()
    case AdministrationCountryPage => navigateToSetUpAfterSettlorDiedIfRegisteredWithDeceasedSettlor
    case HoldoverReliefClaimedPage | EfrbsStartDatePage => _ => firstQuestionAfterTrustTypeQuestions
    case WhyDeedOfVariationCreatedPage => _ => firstQuestionAfterTrustTypeQuestions
    case CreatedUnderScotsLawPage => _ => PreviouslyResidentOffshoreController.onPageLoad()
    case PreviouslyResidentOffshoreCountryPage | AgentCreatedTrustPage => ua => navigateToSchedule3aExemptQuestion(ua)
  }

  private def conditionalNavigation(): PartialFunction[Page, UserAnswers => Call] = {
    case RecordedOnEeaRegisterPage => navigateAwayFromRecordedOnEeaRegisterQuestion
    case SetUpAfterSettlorDiedPage => navigateAwayFromSetUpAfterSettlorDiedQuestion
    case GovernedByUkLawPage => yesNoNav(_, GovernedByUkLawPage, AdministeredInUkController.onPageLoad(), GoverningCountryController.onPageLoad())
    case AdministeredInUkPage => ua => yesNoNav(ua, AdministeredInUkPage, navigateToSetUpAfterSettlorDiedIfRegisteredWithDeceasedSettlor(ua), AdministrationCountryController.onPageLoad())
    case TypeOfTrustPage => navigateAwayFromTypeOfTrustQuestion
    case EfrbsYesNoPage => yesNoNav(_, EfrbsYesNoPage, EfrbsStartDateController.onPageLoad(), firstQuestionAfterTrustTypeQuestions)
    case WhereTrusteesBasedPage => navigateAwayFromWhereTrusteesBasedQuestion
    case SettlorsUkBasedPage => yesNoNav(_, SettlorsUkBasedPage, CreatedUnderScotsLawController.onPageLoad(), BusinessRelationshipInUkController.onPageLoad())
    case PreviouslyResidentOffshorePage => ua => yesNoNav(ua, PreviouslyResidentOffshorePage, PreviouslyResidentOffshoreCountryController.onPageLoad(), navigateToSchedule3aExemptQuestion(ua))
    case SettlorBenefitsFromAssetsPage => ua => yesNoNav(ua, SettlorBenefitsFromAssetsPage, navigateToSchedule3aExemptQuestion(ua), ForPurposeOfSection218Controller.onPageLoad())
    case ForPurposeOfSection218Page => ua => yesNoNav(ua, ForPurposeOfSection218Page, AgentCreatedTrustController.onPageLoad(), navigateToSchedule3aExemptQuestion(ua))
  }

  private def navigateToSetUpAfterSettlorDiedIfRegisteredWithDeceasedSettlor(ua: UserAnswers): Call = {
    if (ua.registeredWithDeceasedSettlor) {
      SetUpAfterSettlorDiedController.onPageLoad()
    } else {
      TypeOfTrustController.onPageLoad()
    }
  }

  private def navigateAwayFromSetUpAfterSettlorDiedQuestion(ua: UserAnswers): Call = {
    if (ua.registeredWithDeceasedSettlor) {
      firstQuestionAfterTrustTypeQuestions
    } else {
      TypeOfTrustController.onPageLoad()
    }
  }

  private def navigateAwayFromRecordedOnEeaRegisterQuestion(ua: UserAnswers): Call = {
    if (ua.migratingFromNonTaxableToTaxable) {
      WhereTrusteesBasedController.onPageLoad()
    } else {
      if (ua.get(TrustResidentInUkPage).contains(true)) {
        navigateToSchedule3aExemptQuestion(ua)
      } else {
        BusinessRelationshipInUkController.onPageLoad()
      }
    }
  }

  private def navigateAwayFromBusinessRelationshipInUkQuestion(ua: UserAnswers): Call = {
    if (ua.migratingFromNonTaxableToTaxable) {
      SettlorBenefitsFromAssetsController.onPageLoad()
    } else {
      navigateToSchedule3aExemptQuestion(ua)
    }
  }

  private def navigateToSchedule3aExemptQuestion(ua: UserAnswers): Call = {
    if (appConfig.schedule3aExemptEnabled && (ua.get(Schedule3aExemptYesNoPage).isDefined || ua.migratingFromNonTaxableToTaxable)) {
      Schedule3aExemptYesNoController.onPageLoad()
    } else {
      CheckDetailsController.onPageLoad()
    }
  }

  private def navigateAwayFromTypeOfTrustQuestion(ua: UserAnswers): Call = {
    ua.get(TypeOfTrustPage) match {
      case Some(InterVivosSettlement) =>
        HoldoverReliefClaimedController.onPageLoad()
      case Some(EmploymentRelated) =>
        EfrbsYesNoController.onPageLoad()
      case Some(DeedOfVariationTrustOrFamilyArrangement) =>
        if (ua.registeredWithDeceasedSettlor) {
          firstQuestionAfterTrustTypeQuestions
        } else {
          WhyDeedOfVariationCreatedController.onPageLoad()
        }
      case Some(WillTrustOrIntestacyTrust) | Some(FlatManagementCompanyOrSinkingFund) | Some(HeritageMaintenanceFund) =>
        firstQuestionAfterTrustTypeQuestions
      case _ =>
        SessionExpiredController.onPageLoad
    }
  }

  private def navigateAwayFromWhereTrusteesBasedQuestion(ua: UserAnswers): Call = {
    ua.get(WhereTrusteesBasedPage) match {
      case Some(AllTrusteesUkBased) => CreatedUnderScotsLawController.onPageLoad()
      case Some(NoTrusteesUkBased) => BusinessRelationshipInUkController.onPageLoad()
      case Some(InternationalAndUkBasedTrustees) => SettlorsUkBasedController.onPageLoad()
      case _ => SessionExpiredController.onPageLoad
    }
  }

  private lazy val firstQuestionAfterTrustTypeQuestions: Call = OwnsUkLandOrPropertyController.onPageLoad()
}
