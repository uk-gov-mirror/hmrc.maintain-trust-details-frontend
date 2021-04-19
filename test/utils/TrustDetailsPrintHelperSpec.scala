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

package utils

import base.SpecBase
import pages.{BusinessRelationshipYesNoPage, TrustEEAYesNoPage, TrustOwnUKLandOrPropertyPage}
import play.twirl.api.Html
import utils.print.TrustDetailsPrintHelper
import viewmodels.{AnswerRow, AnswerSection}

class TrustDetailsPrintHelperSpec extends SpecBase {

  private val printHelper = injector.instanceOf[TrustDetailsPrintHelper]

  "TrustDetailsPrintHelper" must {

    "render answer rows" in {
      val userAnswers = emptyUserAnswers
        .set(TrustOwnUKLandOrPropertyPage, true).success.value
        .set(TrustEEAYesNoPage, false).success.value
        .set(BusinessRelationshipYesNoPage, true).success.value

      val result = printHelper(userAnswers)

      result mustEqual AnswerSection(
        None,
        Seq(
          AnswerRow(messages("trustOwnUKLandOrProperty.checkYourAnswersLabel"), Html("Yes"), Some(controllers.maintain.routes.TrustOwnUKLandOrPropertyController.onPageLoad().url)),
          AnswerRow(messages("trustEEAYesNo.checkYourAnswersLabel"), Html("No"), Some(controllers.maintain.routes.TrustEEAYesNoController.onPageLoad().url)),
          AnswerRow(messages("businessRelationshipYesNo.checkYourAnswersLabel"), Html("Yes"), Some(controllers.maintain.routes.BusinessRelationshipYesNoController.onPageLoad().url))
        ),
        None
      )
    }
  }

}
