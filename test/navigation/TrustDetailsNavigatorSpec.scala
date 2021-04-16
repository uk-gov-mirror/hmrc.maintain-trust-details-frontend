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

import base.SpecBase
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.{BusinessRelationshipYesNoPage, TrustEEAYesNoPage, TrustOwnUKLandOrPropertyPage}

class TrustDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  val navigator = new TrustDetailsNavigator

  "maintain trust details" when {

    "updating" must {

      "TrustOwnUKLandOrProperty Page -> TrustEEAYesNo page" in {
        navigator.nextPage(TrustOwnUKLandOrPropertyPage, emptyUserAnswers)
          .mustBe(controllers.maintain.routes.TrustEEAYesNoController.onPageLoad())
      }

      "TrustEEAYesNo Page -> UK Trust -> CYA page" in {

        val answers = emptyUserAnswers
          .set(???, true).success.value

        navigator.nextPage(TrustEEAYesNoPage, answers)
          .mustBe(controllers.maintain.routes.CheckDetailsController.onPageLoad())
      }

      "TrustEEAYesNo Page -> none UK Trust -> BusinessRelationshipYesNo page" in {

        val answers = emptyUserAnswers
          .set(???, false).success.value

        navigator.nextPage(TrustEEAYesNoPage, answers)
          .mustBe(controllers.maintain.routes.BusinessRelationshipYesNoController.onPageLoad())
      }

      "BusinessRelationshipYesNo Page -> CYA page" in {
        navigator.nextPage(BusinessRelationshipYesNoPage, emptyUserAnswers)
          .mustBe(controllers.maintain.routes.CheckDetailsController.onPageLoad())
      }

    }
  }

}