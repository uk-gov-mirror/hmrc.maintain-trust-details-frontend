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

import org.joda.time.{LocalDate => JodaDate}
import play.api.i18n.Messages
import play.twirl.api.Html
import play.twirl.api.HtmlFormat.escape
import uk.gov.hmrc.play.language.LanguageUtils
import viewmodels.CountryOptions

import java.time.{LocalDate => JavaDate}
import javax.inject.Inject

class CheckAnswersFormatters @Inject()(languageUtils: LanguageUtils,
                                       countryOptions: CountryOptions) {

  def yesOrNo(answer: Boolean)(implicit messages: Messages): Html = {
    if (answer) {
      escape(messages("site.yes"))
    } else {
      escape(messages("site.no"))
    }
  }

  def formatDate(date: JavaDate)(implicit messages: Messages): Html = {
    val convertedDate: JodaDate = new JodaDate(date.getYear, date.getMonthValue, date.getDayOfMonth)
    escape(languageUtils.Dates.formatDate(convertedDate))
  }

  def country(code: String)(implicit messages: Messages): Html = {
    escape(countryOptions.allOptions.find(_.value.equals(code)).map(_.label).getOrElse(""))
  }

  def formatEnum[T](key: String, answer: T)(implicit messages: Messages): Html = {
    escape(messages(s"$key.$answer"))
  }

}
