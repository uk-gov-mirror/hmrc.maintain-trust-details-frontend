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

package views

import forms.TypeOfTrustFormProvider
import models.TypeOfTrust
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.maintain.TypeOfTrustView

class TypeOfTrustViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "typeOfTrust"

  val form = new TypeOfTrustFormProvider()()

  "TypeOfTrustView" when {

    def applyView(form: Form[_]): HtmlFormat.Appendable = {
      val view: TypeOfTrustView = viewFor[TypeOfTrustView](Some(emptyUserAnswers))
      view.apply(form)(fakeRequest, messages)
    }

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithASubmitButton(applyView(form))

    "render radio buttons with hint text" in {

      val doc = asDocument(applyView(form))

      for (option <- TypeOfTrust.options) {
        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
      }
    }

    "render selected radio button" when {

      for (option <- TypeOfTrust.options) {

        s"value is '${option.value}'" must {

          s"have the '${option.value}' radio button selected" in {

            val doc = asDocument(applyView(form.bind(Map("value" -> s"${option.value}"))))

            assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

            for (unselectedOption <- TypeOfTrust.options.filterNot(_ == option)) {
              assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
            }
          }
        }
      }
    }
  }
}