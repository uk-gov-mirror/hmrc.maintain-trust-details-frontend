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

package config

import controllers.routes
import play.api.Configuration
import play.api.i18n.{Lang, Messages}
import play.api.mvc.Call
import uk.gov.hmrc.hmrcfrontend.config.ContactFrontendConfig
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(config: Configuration,
                          servicesConfig: ServicesConfig,
                          contactFrontendConfig: ContactFrontendConfig) {

  val welshLanguageSupportEnabled: Boolean = config.getOptional[Boolean]("features.welsh-language-support").getOrElse(false)

  val en: String = "en"
  val cy: String = "cy"
  val defaultLanguage: Lang = Lang(en)

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang(en),
    "cymraeg" -> Lang(cy)
  )

  def routeToSwitchLanguage: String => Call =
    (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)

  val appName: String = config.get[String]("appName")

  lazy val trustsStoreUrl: String = servicesConfig.baseUrl("trusts-store")

  lazy val loginUrl: String = config.get[String]("urls.login")
  lazy val loginContinueUrl: String = config.get[String]("urls.loginContinue")
  lazy val logoutUrl: String = config.get[String]("urls.logout")
  lazy val maintainATrustOverviewUrl: String = config.get[String]("urls.maintainATrustOverview")

  lazy val trustsUrl: String = servicesConfig.baseUrl("trusts")

  lazy val schedule3aExemptEnabled: Boolean = config.get[Boolean]("microservice.services.features.schedule3aExempt.enabled")

  lazy val logoutAudit: Boolean = config.get[Boolean]("features.auditing.logout")

  lazy val trustsAuthUrl: String = servicesConfig.baseUrl("trusts-auth")

  val betaFeedbackUrl = s"${contactFrontendConfig.baseUrl.get}/contact/beta-feedback?service=${contactFrontendConfig.serviceId.get}"

  lazy val countdownLength: Int = config.get[Int]("timeout.countdown")
  lazy val timeoutLength: Int = config.get[Int]("timeout.length")

  lazy val mongoReplaceIndexes: Boolean = config.get[Boolean]("features.mongo.dropIndexes")
  lazy val mongoSessionTTL: Long = config.get[Int]("mongodb.session.ttlSeconds")
  lazy val mongoPlaybackTTL: Long = config.get[Int]("mongodb.playback.ttlSeconds")


  private def getDate(entry: String): LocalDate = {

    def getInt(path: String): Int = config.get[Int](path)

    LocalDate.of(
      getInt(s"dates.$entry.year"),
      getInt(s"dates.$entry.month"),
      getInt(s"dates.$entry.day")
    )
  }

  lazy val minDate: LocalDate = getDate("minimum")
  lazy val maxDate: LocalDate = getDate("maximum")

  lazy val locationCanonicalList: String = config.get[String]("location.canonical.list.all")
  lazy val locationCanonicalListCY: String = config.get[String]("location.canonical.list.allCY")

  def helplineUrl(implicit messages: Messages): String = {
    val path = messages.lang.code match {
      case `cy` => "urls.welshHelpline"
      case _ => "urls.trustsHelpline"
    }

    config.get[String](path)
  }

}
