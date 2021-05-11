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

import play.api.libs.json._
import viewmodels.RadioOption

sealed trait TrusteesBased

object TrusteesBased {

  case object AllTrusteesUkBased extends WithName("all-uk-based") with TrusteesBased
  case object NoTrusteesUkBased extends WithName("none-uk-based") with TrusteesBased
  case object InternationalAndUkBasedTrustees extends WithName("some-uk-based") with TrusteesBased

  implicit val reads: Reads[TrusteesBased] = Reads {
    case JsString(AllTrusteesUkBased.toString) => JsSuccess(AllTrusteesUkBased)
    case JsString(NoTrusteesUkBased.toString) => JsSuccess(NoTrusteesUkBased)
    case JsString(InternationalAndUkBasedTrustees.toString) => JsSuccess(InternationalAndUkBasedTrustees)
    case _ => JsError("Invalid TrusteesBased")
  }

  implicit val writes: Writes[TrusteesBased] = Writes(x => JsString(x.toString))

  val values: List[TrusteesBased] = List(
    AllTrusteesUkBased, NoTrusteesUkBased, InternationalAndUkBasedTrustees
  )

  val options: List[RadioOption] = values
    .map(value => RadioOption("whereTrusteesBased", value.toString))

  implicit val enumerable: Enumerable[TrusteesBased] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
