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

package pages.maintain

import pages.behaviours.PageBehaviours

import java.time.LocalDate

class EfrbsYesNoPageSpec extends PageBehaviours {

  "EfrbsYesNoPage" must {

    beRetrievable[Boolean](EfrbsYesNoPage)

    beSettable[Boolean](EfrbsYesNoPage)

    beRemovable[Boolean](EfrbsYesNoPage)

    "implement cleanup logic" when {
      "NO selected" in {

        val userAnswers = emptyUserAnswers
          .set(EfrbsYesNoPage, true).success.value
          .set(EfrbsStartDatePage, LocalDate.parse("1996-02-03")).success.value

        val cleanAnswers = userAnswers.set(EfrbsYesNoPage, false).success.value

        cleanAnswers.get(EfrbsStartDatePage) mustBe None
      }
    }
  }
}
