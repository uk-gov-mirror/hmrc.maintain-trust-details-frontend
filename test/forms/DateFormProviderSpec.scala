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

package forms

import forms.behaviours.DateBehaviours
import play.api.data.{Form, FormError}

import java.time.LocalDate

class DateFormProviderSpec extends DateBehaviours {

  private val examplePrefix: String = "efrbsStartDate"

  private val form: Form[LocalDate] = new DateFormProvider(frontendAppConfig).withPrefix(examplePrefix)

  private val min = frontendAppConfig.minDate
  private val max = LocalDate.now()

  "DateFormProvider" should {

    val validData = datesBetween(
      min = min,
      max = max
    )

    behave like dateField(
      form = form,
      key = "value",
      validData = validData
    )

    behave like mandatoryDateField(
      form = form,
      key = "value",
      requiredAllKey = s"$examplePrefix.error.required.all"
    )

    behave like dateFieldWithMax(
      form = form,
      key = "value",
      max = max,
      formError = FormError("value", s"$examplePrefix.error.future", List("day", "month", "year"))
    )

    behave like dateFieldWithMin(
      form = form,
      key = "value",
      min = min,
      formError = FormError("value", s"$examplePrefix.error.past", List("day", "month", "year"))
    )

  }
}
