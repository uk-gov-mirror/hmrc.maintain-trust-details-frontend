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

package config

import base.SpecBase
import play.api.i18n.Lang

class AppConfigSpec extends SpecBase{

  private val appConfig = app.injector.instanceOf[AppConfig]

  "FrontendAppConfig" must {


    "have the correct betaFeedbackUrl" in {
      appConfig.betaFeedbackUrl mustBe "http://localhost:9250/contact/beta-feedback?service=trusts"
    }

    "have the correct loginUrl" in {
      appConfig.loginUrl mustBe "http://localhost:9949/auth-login-stub/gg-sign-in"
    }

    "have the correct loginContinueUrl" in {
      appConfig.loginContinueUrl mustBe "http://localhost:9781/trusts-registration"
    }

    "have the correct languageTranslationEnabled" in {
      appConfig.welshLanguageSupportEnabled mustBe false
    }

    "have the correct mongoSessionTTL" in {
      appConfig.mongoSessionTTL mustBe 3600
    }

    "have the correct mongoPlaybackTTL" in {
      appConfig.mongoPlaybackTTL mustBe 3600
    }

    "have the correct languageMap" in {
      appConfig.languageMap mustBe Map(
        "english" -> Lang("en"),
        "cymraeg" -> Lang("cy")
      )
    }

    "return the correct route to switch language - EN" in {
      val enCall = appConfig.routeToSwitchLanguage("en")
      enCall.url mustBe "/maintain-a-trust/trust-details/language/en"
    }

    "return the correct route to switch language - CY" in {
      val cyCall = appConfig.routeToSwitchLanguage("cy")
      cyCall.url mustBe "/maintain-a-trust/trust-details/language/cy"
    }

    "have the correct helplineURL" in {
      appConfig.helplineUrl mustBe "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/trusts"
    }


  }
}
