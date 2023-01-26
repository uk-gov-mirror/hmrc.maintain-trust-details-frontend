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

import forms.behaviours.OptionFieldBehaviours
import models.TypeOfTrust
import play.api.data.{Form, FormError}

class EnumFormProviderSpec extends OptionFieldBehaviours {

  val prefix = "typeOfTrust"
  val form: Form[TypeOfTrust] = new EnumFormProvider()(prefix)

  "EnumFormProvider" must {

    val fieldName = "value"
    val requiredKey = s"$prefix.error.required"

    behave like optionsField[TypeOfTrust](
      form = form,
      fieldName = fieldName,
      validValues = TypeOfTrust.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
