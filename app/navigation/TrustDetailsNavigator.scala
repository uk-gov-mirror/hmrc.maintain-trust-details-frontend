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

import models.UserAnswers
import pages.Page
import pages.maintain.{BusinessRelationshipInUkPage, OwnsUkLandOrPropertyPage, RecordedOnEeaRegisterPage, TrustResidentInUkPage}
import play.api.mvc.Call

import javax.inject.Inject

class TrustDetailsNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, userAnswers: UserAnswers): Call =
    routes()(page)(userAnswers)

  private def simpleNavigation(): PartialFunction[Page, UserAnswers => Call] = {
    case OwnsUkLandOrPropertyPage => _ => controllers.maintain.routes.RecordedOnEeaRegisterController.onPageLoad()
    case RecordedOnEeaRegisterPage => ua => trustUKResidentPage(ua)
    case BusinessRelationshipInUkPage => _ => controllers.maintain.routes.CheckDetailsController.onPageLoad()
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
}
