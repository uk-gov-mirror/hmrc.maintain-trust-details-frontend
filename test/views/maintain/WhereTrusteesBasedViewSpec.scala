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

package views.maintain

import forms.EnumFormProvider
import models.TrusteesBased
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.EnumViewBehaviours
import views.html.maintain.WhereTrusteesBasedView

class WhereTrusteesBasedViewSpec extends EnumViewBehaviours[TrusteesBased] {

  val messageKeyPrefix = "whereTrusteesBased"

  val form: Form[TrusteesBased] = new EnumFormProvider()(messageKeyPrefix)

  "WhereTrusteesBasedView" when {

    def applyView(form: Form[_]): HtmlFormat.Appendable = {
      val view: WhereTrusteesBasedView = viewFor[WhereTrusteesBasedView](Some(emptyUserAnswers))
      view.apply(form)(fakeRequest, messages)
    }

    behave like normalPage(applyView(form), messageKeyPrefix, "hint")

    behave like pageWithBackLink(applyView(form))

    behave like pageWithASubmitButton(applyView(form))

    behave like pageWithRadioOptions(form, applyView, TrusteesBased.options)
  }
}
