/*
 * Copyright 2022 HM Revenue & Customs
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
import models.DeedOfVariation
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.EnumViewBehaviours
import views.html.maintain.WhyDeedOfVariationCreatedView

class WhyDeedOfVariationCreatedViewSpec extends EnumViewBehaviours[DeedOfVariation] {

  val messageKeyPrefix = "whyDeedOfVariationCreated"

  val form: Form[DeedOfVariation] = new EnumFormProvider()(messageKeyPrefix)

  "WhyDeedOfVariationCreatedView" when {

    def applyView(form: Form[_]): HtmlFormat.Appendable = {
      val view: WhyDeedOfVariationCreatedView = viewFor[WhyDeedOfVariationCreatedView](Some(emptyUserAnswers))
      view.apply(form)(fakeRequest, messages)
    }

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithASubmitButton(applyView(form))

    behave like pageWithRadioOptions(form, applyView, DeedOfVariation.options)
  }
}
