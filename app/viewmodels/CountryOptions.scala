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

package viewmodels

import com.typesafe.config.ConfigException
import config.AppConfig
import models.Constants.GB
import play.api.Environment
import play.api.i18n.Messages
import play.api.libs.json.Json

import javax.inject.{Inject, Singleton}

@Singleton
class CountryOptions @Inject()(environment: Environment, config: AppConfig) {

  def allOptions(implicit messages: Messages): Seq[InputOption] = {
    getCountriesForFileName(fileName)
  }

  def nonUkOptions(implicit messages: Messages): Seq[InputOption] = {
    allOptions.filterNot(_.value == GB)
  }

  private def getCountriesForFileName(fileName: String): Seq[InputOption] = {
    environment.resourceAsStream(fileName).flatMap {
      in =>
        val locationJsValue = Json.parse(in)
        Json.fromJson[Seq[Seq[String]]](locationJsValue).asOpt.map {
          _.map { countryList =>
            InputOption(countryList(1).replaceAll("country:", ""), countryList.head)
          }.sortBy(x => x.label.toLowerCase)
        }
    }.getOrElse {
      throw new ConfigException.BadValue(fileName, "country json does not exist")
    }
  }

  private def fileName()(implicit messages: Messages): String = {
    val inWelshMode = messages.lang.code == config.cy
    if (inWelshMode) config.locationCanonicalListCY else config.locationCanonicalList
  }

}
