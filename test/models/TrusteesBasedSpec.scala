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

package models

import base.SpecBase
import models.TrusteesBased._
import play.api.libs.json.{JsNull, JsString, Json}
import viewmodels.RadioOption

class TrusteesBasedSpec extends SpecBase{

  "TrusteesBased" must {

    "serialise and deserailise" when {

      "AllTrusteesUkBased" in {
        val json = JsString("all-uk-based")
        val trusteesBased = json.as[TrusteesBased]
        trusteesBased mustEqual AllTrusteesUkBased
        Json.toJson(trusteesBased) mustEqual json
      }

      "NoTrusteesUkBased" in {
        val json = JsString("none-uk-based")
        val trusteesBased = json.as[TrusteesBased]
        trusteesBased mustEqual NoTrusteesUkBased
        Json.toJson(trusteesBased) mustEqual json
      }

      "InternationalAndUkBasedTrustees" in {
        val json = JsString("some-uk-based")
        val trusteesBased = json.as[TrusteesBased]
        trusteesBased mustEqual InternationalAndUkBasedTrustees
        Json.toJson(trusteesBased) mustEqual json
      }
    }

    "return error when reading invalid trustees based" in {
      val json = JsNull
      json.validate[TrusteesBased].isError mustBe true
    }

    "render radio options" in {
      val prefix = "whereTrusteesBased"
      TrusteesBased.options mustEqual List(
        RadioOption(prefix, "all-uk-based"),
        RadioOption(prefix, "none-uk-based"),
        RadioOption(prefix, "some-uk-based")
      )
    }
  }
}
