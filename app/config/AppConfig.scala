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

package config

import play.api.Configuration
import play.api.i18n.{Lang, Messages}
import play.api.mvc.{Call, Request}
import controllers.routes
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.net.{URI, URLEncoder}
import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(config: Configuration, servicesConfig: ServicesConfig) {

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

  lazy val trustsStoreUrl: String = servicesConfig.baseUrl("trusts-store")

  lazy val loginUrl: String = config.get[String]("urls.login")
  lazy val loginContinueUrl: String = config.get[String]("urls.loginContinue")
  lazy val logoutUrl: String = config.get[String]("urls.logout")
  lazy val maintainATrustOverviewUrl: String = config.get[String]("urls.maintainATrustOverview")

  lazy val logoutAudit: Boolean = config.get[Boolean]("features.auditing.logout")

  lazy val trustsAuthUrl: String = servicesConfig.baseUrl("trusts-auth")

  private lazy val contactHost: String = config.get[String]("microservice.services.contact-frontend.host")
  private lazy val contactFormServiceIdentifier: String = "trusts"

  lazy val betaFeedbackUrl: String = s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier"
  lazy val betaFeedbackUnauthenticatedUrl: String = s"$contactHost/contact/beta-feedback-unauthenticated?service=$contactFormServiceIdentifier"

  lazy val reportAProblemPartialUrl: String = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  lazy val reportAProblemNonJSUrl: String = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"

  def accessibilityLinkUrl(implicit request: Request[_]): String = {
    lazy val accessibilityBaseLinkUrl: String = config.get[String]("urls.accessibility")
    val userAction = URLEncoder.encode(new URI(request.uri).getPath, "UTF-8")
    s"$accessibilityBaseLinkUrl?userAction=$userAction"
  }

  val analyticsToken: String  = config.get[String](s"google-analytics.token")

  lazy val countdownLength: String = config.get[String]("timeout.countdown")
  lazy val timeoutLength: String = config.get[String]("timeout.length")

  def helplineUrl(implicit messages: Messages): String = {
    val path = messages.lang.code match {
      case `cy` => "urls.welshHelpline"
      case _ => "urls.trustsHelpline"
    }

    config.get[String](path)
  }

}
