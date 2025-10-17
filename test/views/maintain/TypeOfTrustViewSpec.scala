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

package views.maintain

import forms.EnumFormProvider
import models.TypeOfTrust
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.EnumViewBehaviours
import views.html.maintain.TypeOfTrustView

class TypeOfTrustViewSpec extends EnumViewBehaviours[TypeOfTrust] {

  val messageKeyPrefix = "typeOfTrust"

  val form: Form[TypeOfTrust] = new EnumFormProvider()(messageKeyPrefix)

  "TypeOfTrustView" when {

    def applyView(form: Form[_]): HtmlFormat.Appendable = {
      val view: TypeOfTrustView = viewFor[TypeOfTrustView](Some(emptyUserAnswers))
      view.apply(form)(fakeRequest, messages)
    }

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithASubmitButton(applyView(form))

    behave like pageWithRadioOptions(form, applyView, TypeOfTrust.options)
  }
}
