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

package views.behaviours

import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.RadioOption

trait EnumViewBehaviours[A] extends ViewBehaviours {

  def pageWithRadioOptions(form: Form[A],
                           applyView: Form[A] => HtmlFormat.Appendable,
                           options: List[RadioOption]): Unit = {

    "render radio buttons" in {

      val doc = asDocument(applyView(form))

      for (option <- options) {
        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
      }
    }

    "render selected radio button" when {

      for (option <- options) {

        s"value is '${option.value}'" must {

          s"have the '${option.value}' radio button selected" in {

            val doc = asDocument(applyView(form.bind(Map("value" -> s"${option.value}"))))

            assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

            for (unselectedOption <- options.filterNot(_ == option)) {
              assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
            }
          }
        }
      }
    }
  }
}
