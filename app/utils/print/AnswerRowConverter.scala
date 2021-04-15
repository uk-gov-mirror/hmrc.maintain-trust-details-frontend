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

package utils.print


import com.google.inject.Inject
import models.UserAnswers
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.twirl.api.{Html, HtmlFormat}
import queries.Gettable
import viewmodels.AnswerRow

class AnswerRowConverter @Inject()(checkAnswersFormatters: CheckAnswersFormatters) {

  def bind(userAnswers: UserAnswers)
          (implicit messages: Messages): Bound = new Bound(userAnswers)

  class Bound(userAnswers: UserAnswers)(implicit messages: Messages) {



    def yesNoQuestion(query: Gettable[Boolean],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      val format = (x: Boolean) => checkAnswersFormatters.yesOrNo(x)
      question(query, labelKey, format, changeUrl)
    }

    private def question[T](query: Gettable[T],
                            labelKey: String,
                            format: T => Html,
                            changeUrl: String)
                           (implicit rds: Reads[T]): Option[AnswerRow] = {
//      userAnswers.get(query) map { x =>
//        AnswerRow(
//          label = HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel")),
//          answer = format(x),
//          changeUrl = changeUrl
//        )
//      }
      None
    }
  }
}
