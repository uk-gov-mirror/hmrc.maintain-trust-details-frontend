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

import base.SpecBase
import pages.maintain.{BusinessRelationshipInUkPage, RecordedOnEeaRegisterPage, OwnsUkLandOrPropertyPage}
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class TrustDetailsPrintHelperSpec extends SpecBase {

  private val printHelper = injector.instanceOf[TrustDetailsPrintHelper]

  "TrustDetailsPrintHelper" must {

    "render answer rows" when {

      "migrating from non-taxable to taxable" ignore {
        // TODO - fill in unit test once print helper updated
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
            AnswerRow(messages("ownsUkLandOrProperty.checkYourAnswersLabel"), Html("Yes"), Some(controllers.maintain.routes.OwnsUkLandOrPropertyController.onPageLoad().url)),
            AnswerRow(messages("recordedOnEeaRegister.checkYourAnswersLabel"), Html("No"), Some(controllers.maintain.routes.RecordedOnEeaRegisterController.onPageLoad().url)),
            AnswerRow(messages("businessRelationshipInUk.checkYourAnswersLabel"), Html("Yes"), Some(controllers.maintain.routes.BusinessRelationshipInUkController.onPageLoad().url))
          ),
          sectionKey = None
        )
      }
    }
  }

}
