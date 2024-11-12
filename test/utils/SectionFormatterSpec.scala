/*
 * Copyright 2024 HM Revenue & Customs
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
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.Aliases.Actions
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Key, SummaryListRow, Value}
import viewmodels.{AnswerRow, AnswerSection}

class SectionFormatterSpec extends SpecBase {

  "SectionFormatter" must {

    "format section as series of summary list rows" in {

      val answerSection: AnswerSection = AnswerSection(
        headingKey = None,
        rows = Seq(
          AnswerRow(messages("startDate.checkYourAnswersLabel"), Html("1 January 2000"), canEdit = false, changeUrl = Some("/change-url")),
          AnswerRow(messages("governedByUkLawYesNo.checkYourAnswersLabel"), Html("No"), Some("/change-url"))),
        sectionKey = None
      )

      val result = SectionFormatter.formatAnswerSection(answerSection)

      result mustEqual Seq(
        SummaryListRow(
          key = Key(classes = "govuk-!-width-two-thirds",
            content = Text("When was the trust created?")
          ),
          value = Value(classes = "govuk-!-width-one-half", content = HtmlContent("1 January 2000")),
          actions = None
        ),
        SummaryListRow(
          key = Key(
            classes = "govuk-!-width-two-thirds",
            content = Text("Is the trust governed by UK law?")
          ),
          value = Value(classes = "govuk-!-width-one-half", content = HtmlContent("No")),
          actions = Option(Actions(items = Seq(ActionItem(href = "/change-url",
            classes = s"change-link-1",
            visuallyHiddenText = Some("Is the trust governed by UK law?"),
            content = Text(messages("site.edit"))
          ))))
        )
      )
    }
  }
}


