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

import forms.CountryFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.{CountryOptions, InputOption}
import views.behaviours.SelectCountryViewBehaviours
import views.html.maintain.GoverningCountryView

class GoverningCountryViewSpec extends SelectCountryViewBehaviours {

  val messageKeyPrefix = "governingCountry"

  val form: Form[String] = new CountryFormProvider().withPrefix(messageKeyPrefix)

  "GoverningCountryView" must {

    val view = viewFor[GoverningCountryView](Some(emptyUserAnswers))

    val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptions].nonUkOptions

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, countryOptions)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like selectCountryPage(form, applyView, messageKeyPrefix)

    behave like pageWithASubmitButton(applyView(form))
  }
}
