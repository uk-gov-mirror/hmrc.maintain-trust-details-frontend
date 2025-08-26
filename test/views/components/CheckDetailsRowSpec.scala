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

package views.components

import base.SpecBase
import play.twirl.api.Html
import viewmodels.AnswerRow

class CheckDetailsRowSpec extends SpecBase {

  "render as a row with details" in {

    val checkDetailsRow = app.injector.instanceOf[views.html.components.CheckDetailsRow]
    val ansRow = AnswerRow(messages("startDate.checkYourAnswersLabel"),
      Html("1 January 2000"), canEdit = false, changeUrl = Some("/change-url"))

    val result = checkDetailsRow.apply(ansRow)(messages)

    result.body must include("govuk-summary-list__row")
    result.body must include("When was the trust created")
  }
}