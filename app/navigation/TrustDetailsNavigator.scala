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

import models.{TypeOfTrust, UserAnswers}
import pages.Page
import pages.maintain.{BusinessRelationshipInUkPage, OwnsUkLandOrPropertyPage, RecordedOnEeaRegisterPage, SetUpAfterSettlorDiedPage, TrustResidentInUkPage, TypeOfTrustPage}
import play.api.mvc.Call
import javax.inject.Inject

class TrustDetailsNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, userAnswers: UserAnswers): Call =
    routes()(page)(userAnswers)

  private def simpleNavigation(): PartialFunction[Page, UserAnswers => Call] = {
    case OwnsUkLandOrPropertyPage => _ => controllers.maintain.routes.RecordedOnEeaRegisterController.onPageLoad()
    case RecordedOnEeaRegisterPage => ua => trustUKResidentPage(ua)
    case BusinessRelationshipInUkPage => _ => controllers.maintain.routes.CheckDetailsController.onPageLoad()
    case SetUpAfterSettlorDiedPage => ua => fromSetUpAfterSettlorDiedPage(ua)
    case TypeOfTrustPage => ua => fromTypeOfTrustPage(ua)
  }

  def routes(): PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation()


  private def trustUKResidentPage(ua: UserAnswers): Call = {
    if (ua.get(TrustResidentInUkPage).contains(true)) {
      controllers.maintain.routes.CheckDetailsController.onPageLoad()
    } else {
      controllers.maintain.routes.BusinessRelationshipInUkController.onPageLoad()
    }
  }

  private def fromSetUpAfterSettlorDiedPage(ua: UserAnswers): Call = {
    ua.get(SetUpAfterSettlorDiedPage) match {
      case Some(true) =>
        controllers.maintain.routes.WhereTrusteesBasedController.onPageLoad()
      case Some(false) =>
        controllers.maintain.routes.TypeOfTrustController.onPageLoad()
      case _ =>
        controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def fromTypeOfTrustPage(ua: UserAnswers): Call = {
    //ToDo Add Navigation to the relevant pages
    ua.get(TypeOfTrustPage) match {
      case Some(TypeOfTrust.InterVivosSettlement) =>
        controllers.routes.FeatureNotAvailableController.onPageLoad()
      case Some(TypeOfTrust.EmploymentRelated) =>
        controllers.routes.FeatureNotAvailableController.onPageLoad()
      case Some(TypeOfTrust.DeedOfVariationTrustOrFamilyArrangement) =>
        controllers.routes.FeatureNotAvailableController.onPageLoad()
      case Some(TypeOfTrust.FlatManagementCompanyOrSinkingFund) =>
        controllers.routes.FeatureNotAvailableController.onPageLoad()
      case Some(TypeOfTrust.HeritageMaintenanceFund) =>
        controllers.routes.FeatureNotAvailableController.onPageLoad()
      case Some(TypeOfTrust.WillTrustOrIntestacyTrust) =>
        controllers.routes.FeatureNotAvailableController.onPageLoad()
      case _ =>
        controllers.routes.SessionExpiredController.onPageLoad()
    }
  }
}
