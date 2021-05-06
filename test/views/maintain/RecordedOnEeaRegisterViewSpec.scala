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

import controllers.maintain.routes
import forms.YesNoFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.maintain.RecordedOnEeaRegisterView

class RecordedOnEeaRegisterViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "recordedOnEeaRegister"

  val form: Form[Boolean] = new YesNoFormProvider().withPrefix(messageKeyPrefix)

  "TrustEEAYesNoView view" must {

    val view = viewFor[RecordedOnEeaRegisterView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form)(fakeRequest, messages)

    behave like normalPage(
      view = applyView(form),
      messageKeyPrefix = messageKeyPrefix,
      expectedGuidanceKeys = "paragraph1", "bullet1", "bullet2", "bullet3", "bullet4"
    )

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, None, routes.RecordedOnEeaRegisterController.onSubmit().url)

    behave like pageWithASubmitButton(applyView(form))
  }
}
