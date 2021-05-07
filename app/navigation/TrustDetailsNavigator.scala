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

package navigation

import controllers.maintain.routes._
import controllers.routes.SessionExpiredController
import models.{TypeOfTrust, UserAnswers}
import pages.Page
import pages.maintain._
import play.api.mvc.Call

import javax.inject.Inject

class TrustDetailsNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, userAnswers: UserAnswers): Call =
    routes()(page)(userAnswers)

  private def routes(): PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation() orElse
      conditionalNavigation()

  private def simpleNavigation(): PartialFunction[Page, UserAnswers => Call] = {
    case OwnsUkLandOrPropertyPage => _ => RecordedOnEeaRegisterController.onPageLoad()
    case BusinessRelationshipInUkPage => _ => CheckDetailsController.onPageLoad()
    case HoldoverReliefClaimedPage | EfrbsStartDatePage => _ => WhereTrusteesBasedController.onPageLoad()
  }

  private def conditionalNavigation(): PartialFunction[Page, UserAnswers => Call] = {
    case RecordedOnEeaRegisterPage => trustUKResidentPage
    case SetUpAfterSettlorDiedPage => fromSetUpAfterSettlorDiedPage
    case TypeOfTrustPage => fromTypeOfTrustPage
    case EfrbsYesNoPage => yesNoNav(_, EfrbsYesNoPage, EfrbsStartDateController.onPageLoad(), WhereTrusteesBasedController.onPageLoad())
  }

  private def trustUKResidentPage(ua: UserAnswers): Call = {
    if (ua.get(TrustResidentInUkPage).contains(true)) {
      CheckDetailsController.onPageLoad()
    } else {
      BusinessRelationshipInUkController.onPageLoad()
    }
  }

  private def fromSetUpAfterSettlorDiedPage(ua: UserAnswers): Call = {
    ua.get(SetUpAfterSettlorDiedPage) match {
      case Some(true) =>
        WhereTrusteesBasedController.onPageLoad()
      case Some(false) =>
        TypeOfTrustController.onPageLoad()
      case _ =>
        SessionExpiredController.onPageLoad()
    }
  }

  private def fromTypeOfTrustPage(ua: UserAnswers): Call = {
    ua.get(TypeOfTrustPage) match {
      case Some(TypeOfTrust.InterVivosSettlement) =>
        HoldoverReliefClaimedController.onPageLoad()
      case Some(TypeOfTrust.EmploymentRelated) =>
        EfrbsYesNoController.onPageLoad()
      case Some(TypeOfTrust.DeedOfVariationTrustOrFamilyArrangement) =>
        controllers.routes.FeatureNotAvailableController.onPageLoad() // TODO - redirect to 'set up after settlor died y/n'
      case Some(TypeOfTrust.WillTrustOrIntestacyTrust) | Some(TypeOfTrust.FlatManagementCompanyOrSinkingFund) | Some(TypeOfTrust.HeritageMaintenanceFund) =>
        WhereTrusteesBasedController.onPageLoad()
      case _ =>
        SessionExpiredController.onPageLoad()
    }
  }
}
