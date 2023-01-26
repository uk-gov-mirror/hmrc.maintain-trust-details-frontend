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

package forms

import play.api.data.{Form, FormError}
import forms.behaviours.BooleanFieldBehaviours

class YesNoFormProviderSpec extends BooleanFieldBehaviours {

  val messagePrefix = "yesNo"
  val requiredKey = s"$messagePrefix.error.required"
  val invalidKey = "error.boolean"

  val form: Form[Boolean] = new YesNoFormProvider().withPrefix(messagePrefix)

  "YesNoFormProvider" must {

    val fieldName = "value"

    behave like booleanField(
      form = form,
      fieldName = fieldName,
      invalidError = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

}
