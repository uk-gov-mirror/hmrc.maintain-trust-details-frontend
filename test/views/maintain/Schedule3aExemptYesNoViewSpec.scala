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

package views.maintain

import forms.YesNoFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.Schedule3aExemptYesNoViewBehaviours
import views.html.maintain.Schedule3aExemptYesNoView

class Schedule3aExemptYesNoViewSpec extends Schedule3aExemptYesNoViewBehaviours {

  private val messageKeyPrefix = "schedule3aExemptYesNo"

  val form: Form[Boolean] = new YesNoFormProvider().withPrefix(messageKeyPrefix)

  "Schedule3aExemptYesNoView" must {

    val view = viewFor[Schedule3aExemptYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form)(fakeRequest, messages)

    behave like normalPage(
      view = applyView(form),
      messageKeyPrefix = messageKeyPrefix,
      expectedGuidanceKeys = "p1", "p2", "p3",
      "bullet1", "bullet2", "bullet3", "bullet4",
      "bullet5", "bullet6", "bullet7", "bullet8",
      "bullet9", "bullet10", "bullet11", "bullet12",
      "bullet13", "bullet14", "bullet15", "bullet16",
      "subheading"
    )

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix)

    behave like pageWithASubmitButton(applyView(form))
  }
}
