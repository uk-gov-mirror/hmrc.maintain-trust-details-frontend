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

package models

import base.SpecBase
import models.TypeOfTrust._
import play.api.libs.json.{JsNull, JsString, Json}
import viewmodels.RadioOption

class TypeOfTrustSpec extends SpecBase{

  "TypeOfTrust" must {

    "serialise and deserailise" when {

      "Will Trust or Intestacy Trust" in {
        val json = JsString("Will Trust or Intestacy Trust")
        val typeOfTrust = json.as[TypeOfTrust]
        typeOfTrust mustEqual WillTrustOrIntestacyTrust
        Json.toJson(typeOfTrust) mustEqual json
      }

      "Deed of Variation Trust or Family Arrangement" in {
        val json = JsString("Deed of Variation Trust or Family Arrangement")
        val typeOfTrust = json.as[TypeOfTrust]
        typeOfTrust mustEqual DeedOfVariationTrustOrFamilyArrangement
        Json.toJson(typeOfTrust) mustEqual json
      }

      "Inter vivos Settlement" in {
        val json = JsString("Inter vivos Settlement")
        val typeOfTrust = json.as[TypeOfTrust]
        typeOfTrust mustEqual InterVivosSettlement
        Json.toJson(typeOfTrust) mustEqual json
      }

      "Employment Related" in {
        val json = JsString("Employment Related")
        val typeOfTrust = json.as[TypeOfTrust]
        typeOfTrust mustEqual EmploymentRelated
        Json.toJson(typeOfTrust) mustEqual json
      }

      "Heritage Maintenance Fund" in {
        val json = JsString("Heritage Maintenance Fund")
        val typeOfTrust = json.as[TypeOfTrust]
        typeOfTrust mustEqual HeritageMaintenanceFund
        Json.toJson(typeOfTrust) mustEqual json
      }

      "Flat Management Company or Sinking Fund" in {
        val json = JsString("Flat Management Company or Sinking Fund")
        val typeOfTrust = json.as[TypeOfTrust]
        typeOfTrust mustEqual FlatManagementCompanyOrSinkingFund
        Json.toJson(typeOfTrust) mustEqual json
      }
    }

    "return error when reading invalid type of trust" in {
      val json = JsNull
      json.validate[TypeOfTrust].isError mustBe true
    }

    "filter out WillTrustOrIntestacyTrust in radio options" in {
      val prefix = "typeOfTrust"
      TypeOfTrust.options mustEqual List(
        RadioOption(prefix, "inter-vivos"),
        RadioOption(prefix, "deed-of-variation"),
        RadioOption(prefix, "employment-related"),
        RadioOption(prefix, "flat-or-sinking-fund"),
        RadioOption(prefix, "heritage")
      )
    }
  }
}
