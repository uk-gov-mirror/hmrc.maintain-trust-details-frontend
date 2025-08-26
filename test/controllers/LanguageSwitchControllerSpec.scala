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

package controllers

import base.SpecBase
import config.AppConfig
import play.api.i18n.Lang
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.play.language.LanguageUtils

class LanguageSwitchControllerSpec extends SpecBase {

  implicit val cc: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]
  val appconfig = app.injector.instanceOf[AppConfig]
  val langUtil = app.injector.instanceOf[LanguageUtils]

  "LanguageSwitchController" must {

    "return login url" in {

      val result = new LanguageSwitchController(appconfig, messagesApi, langUtil, cc).fallbackURL
      result mustBe "http://localhost:9781/trusts-registration"
    }

    "return languageMap" in {

      val result = new LanguageSwitchController(appconfig, messagesApi, langUtil, cc).languageMap
      result mustBe Map("english" -> Lang("en"), "cymraeg" -> Lang("cy"))
    }

  }
}
