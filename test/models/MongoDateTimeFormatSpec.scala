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

package models

import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json._
import play.api.libs.json.Json

import java.time.{LocalDate, LocalDateTime}

class MongoDateTimeFormatsSpec extends AnyFreeSpec with Matchers with OptionValues with MongoDateTimeFormats {

  "a LocalDateTime" - {
    val date = LocalDate.of(2018, 2, 1).atStartOfDay
    val dateMillis = 1517443200000L
    val json = Json.obj(
      s"$$date" -> dateMillis
    )
    "must serialise to json" in {
      val result = Json.toJson(date)
      result mustEqual json
    }
    "must deserialise from json" in {
      val result = json.as[LocalDateTime]
      result mustEqual date
    }
    "must serialise/deserialise to the same value" in {
      val result = Json.toJson(date).as[LocalDateTime]
      result mustEqual date
    }

    "must deserialise from json when $date contains $numberLong" in {
      val jsonNumberLong = Json.obj(
        "$date" -> Json.obj("$numberLong" -> JsString(dateMillis.toString))
      )
      val result = jsonNumberLong.as[LocalDateTime]
      result mustEqual date
    }

    "must deserialise from json when $date is an ISO string with Z" in {
      val jsonIsoZ = Json.obj("$date" -> JsString("2018-02-01T00:00:00Z"))
      val result = jsonIsoZ.as[LocalDateTime]
      result mustEqual date
    }

    "must deserialise from json when $date is an ISO string without Z" in {
      val jsonIsoNoZ = Json.obj("$date" -> JsString("2018-02-01T00:00:00"))
      val result = jsonIsoNoZ.as[LocalDateTime]
      result mustEqual date
    }

    "must fail to deserialise when $date is the wrong type" in {
      val bad = Json.obj("$date" -> JsBoolean(true))
      val result = bad.validate[LocalDateTime]
      result.isError mustBe true
      val JsError(errs) = result
      errs.head._2.head.message mustBe "Unexpected LocalDateTime Format"
    }

    "must fail to deserialise when $date object does not contain $numberLong" in {
      val bad = Json.obj("$date" -> Json.obj("notNumberLong" -> JsString("x")))
      val result = bad.validate[LocalDateTime]
      result.isError mustBe true
      val JsError(errs) = result
      errs.head._2.head.message mustBe "Unexpected LocalDateTime Format"
    }

    "must fail to deserialise when $date is missing" in {
      val bad = Json.obj("notDate" -> JsNumber(dateMillis))
      val result = bad.validate[LocalDateTime]
      result.isError mustBe true
      val JsError(errs) = result
      errs.head._2.head.message mustBe "Unexpected LocalDateTime Format"
    }
  }
}
