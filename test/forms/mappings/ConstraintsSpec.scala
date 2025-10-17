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

package forms.mappings

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.data.validation.{Invalid, Valid}
import java.time.LocalDate

class ConstraintsSpec extends AnyWordSpec with Matchers with Constraints {

  "firstError" must {

    "return Valid when all constraints pass" in {
      val result = firstError(maxLength(10, "error.length"), regexp("""^\w+$""", "error.regexp"))("foo")
      result mustEqual Valid
    }

    "return Invalid when the first constraint fails" in {
      val result = firstError(maxLength(2, "error.length"), regexp("""^\w+$""", "error.regexp"))("abcd")
      result mustEqual Invalid("error.length", 2)
    }

    "return Invalid when the second constraint fails" in {
      val result = firstError(maxLength(10, "error.length"), regexp("""^\d+$""", "error.regexp"))("abc")
      result mustEqual Invalid("error.regexp", """^\d+$""")
    }

    "return Valid if both constraints are valid" in {
      val result = firstError(maxLength(5, "error.length"), regexp("""^\w+$""", "error.regexp"))("test")
      result mustEqual Valid
    }
  }

  "minimumValue" must {

    "return Valid for a number greater than the threshold" in {
      val result = minimumValue(1, "error.min").apply(2)
      result mustEqual Valid
    }

    "return Valid for a number equal to the threshold" in {
      val result = minimumValue(1, "error.min").apply(1)
      result mustEqual Valid
    }

    "return Invalid for a number below the threshold" in {
      val result = minimumValue(1, "error.min").apply(0)
      result mustEqual Invalid("error.min", 1)
    }
  }

  "maximumValue" must {

    "return Valid for a number less than the threshold" in {
      val result = maximumValue(5, "error.max").apply(4)
      result mustEqual Valid
    }

    "return Valid for a number equal to the threshold" in {
      val result = maximumValue(5, "error.max").apply(5)
      result mustEqual Valid
    }

    "return Invalid for a number above the threshold" in {
      val result = maximumValue(5, "error.max").apply(6)
      result mustEqual Invalid("error.max", 5)
    }
  }

  "inRange" must {

    "return Valid when value is within range" in {
      val result = inRange(1, 5, "error.range").apply(3)
      result mustEqual Valid
    }

    "return Invalid when value is below minimum" in {
      val result = inRange(1, 5, "error.range").apply(0)
      result mustEqual Invalid("error.range", 1, 5)
    }

    "return Invalid when value is above maximum" in {
      val result = inRange(1, 5, "error.range").apply(6)
      result mustEqual Invalid("error.range", 1, 5)
    }
  }

  "regexp" must {

    "return Valid when string matches the regex" in {
      val result = regexp("""^\d+$""", "error.regex")("12345")
      result mustEqual Valid
    }

    "return Invalid when string does not match the regex" in {
      val result = regexp("""^\d+$""", "error.regex")("abc")
      result mustEqual Invalid("error.regex", """^\d+$""")
    }
  }

  "maxLength" must {

    "return Valid for a string shorter than the allowed length" in {
      val result = maxLength(10, "error.length")("a" * 9)
      result mustEqual Valid
    }

    "return Valid for a string equal to the allowed length" in {
      val result = maxLength(10, "error.length")("a" * 10)
      result mustEqual Valid
    }

    "return Invalid for a string longer than the allowed length" in {
      val result = maxLength(10, "error.length")("a" * 11)
      result mustEqual Invalid("error.length", 10)
    }
  }

  "minLength" must {

    "return Valid for a string longer than the minimum length" in {
      val result = minLength(3, "error.minLength")("abcd")
      result mustEqual Valid
    }

    "return Valid for a string equal to the minimum length" in {
      val result = minLength(3, "error.minLength")("abc")
      result mustEqual Valid
    }

    "return Invalid for a string shorter than the minimum length" in {
      val result = minLength(3, "error.minLength")("a")
      result mustEqual Invalid("error.minLength", 3)
    }
  }

  "maxDate" must {

    "return Valid when date is before maximum" in {
      val result = maxDate(LocalDate.now(), "error.max")(LocalDate.now().minusDays(1))
      result mustEqual Valid
    }

    "return Valid when date is equal to maximum" in {
      val today = LocalDate.now()
      val result = maxDate(today, "error.max")(today)
      result mustEqual Valid
    }

    "return Invalid when date is after maximum" in {
      val today = LocalDate.now()
      val result = maxDate(today, "error.max")(today.plusDays(1))
      result mustEqual Invalid("error.max")
    }
  }

  "minDate" must {

    "return Valid when date is after minimum" in {
      val today = LocalDate.now()
      val result = minDate(today.minusDays(1), "error.min")(today)
      result mustEqual Valid
    }

    "return Valid when date is equal to minimum" in {
      val today = LocalDate.now()
      val result = minDate(today, "error.min")(today)
      result mustEqual Valid
    }

    "return Invalid when date is before minimum" in {
      val today = LocalDate.now()
      val result = minDate(today, "error.min")(today.minusDays(1))
      result mustEqual Invalid("error.min")
    }
  }

  "nonEmptySet" must {

    "return Valid for non-empty Set" in {
      val result = nonEmptySet("error.empty")(Set(1))
      result mustEqual Valid
    }

    "return Invalid for empty Set" in {
      val result = nonEmptySet("error.empty")(Set.empty)
      result mustEqual Invalid("error.empty")
    }
  }

  "nonEmptyString" must {

    "return Valid for a non-empty trimmed string" in {
      val result = nonEmptyString("value", "error.nonEmpty")("text")
      result mustEqual Valid
    }

    "return Invalid for an empty string" in {
      val result = nonEmptyString("value", "error.nonEmpty")("")
      result mustEqual Invalid("error.nonEmpty", "value")
    }

    "return Invalid for a string with only spaces" in {
      val result = nonEmptyString("value", "error.nonEmpty")("   ")
      result mustEqual Invalid("error.nonEmpty", "value")
    }
  }
}
