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

import play.api.libs.json.{JsError, JsString, JsSuccess, Reads, Writes}
import viewmodels.RadioOption

sealed trait DeedOfVariation {
  val asString: String
}

object DeedOfVariation {

  case object ReplacedWillTrust extends WithName("replace-will-trust") with DeedOfVariation {
    override val asString: String = "Replaced the will trust"
  }

  case object PreviouslyAbsoluteInterestUnderWill extends WithName("replace-absolute-interest") with DeedOfVariation {
    override val asString: String = "Previously there was only an absolute interest under the will"
  }

  case object AdditionToWillTrust extends WithName("add-will-trust") with DeedOfVariation {
    override val asString: String = "Addition to the will trust"
  }

  implicit val reads: Reads[DeedOfVariation] = Reads {
    case JsString(PreviouslyAbsoluteInterestUnderWill.asString) => JsSuccess(PreviouslyAbsoluteInterestUnderWill)
    case JsString(ReplacedWillTrust.asString) => JsSuccess(ReplacedWillTrust)
    case JsString(AdditionToWillTrust.asString) => JsSuccess(AdditionToWillTrust)
    case _ => JsError("Invalid DeedOfVariation")
  }

  implicit val writes: Writes[DeedOfVariation] = Writes(x => JsString(x.asString))

  val values: List[DeedOfVariation] = List(
    ReplacedWillTrust,
    PreviouslyAbsoluteInterestUnderWill,
    AdditionToWillTrust
  )

  val options: List[RadioOption] = values
    .filterNot(_ == AdditionToWillTrust)
    .map(value => RadioOption("whyDeedOfVariationCreated", value.toString))

  implicit val enumerable: Enumerable[DeedOfVariation] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
