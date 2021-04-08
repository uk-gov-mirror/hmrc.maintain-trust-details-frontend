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

package utils

import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier

trait SessionLogging extends Logging {

  private def logInfo(identifier: Option[String])(implicit hc: HeaderCarrier): String = {
    s"[Session ID: ${Session.id}]" + identifier.fold("")(x => s"[Identifier: $x]")
  }

  def infoLog(message: String, identifier: Option[String] = None)(implicit hc: HeaderCarrier): Unit =
    logger.info(s"${logInfo(identifier)} $message")

  def warnLog(message: String, identifier: Option[String] = None)(implicit hc: HeaderCarrier): Unit =
    logger.warn(s"${logInfo(identifier)} $message")

  def errorLog(message: String, identifier: Option[String] = None)(implicit hc: HeaderCarrier): Unit =
    logger.error(s"${logInfo(identifier)} $message")

}
