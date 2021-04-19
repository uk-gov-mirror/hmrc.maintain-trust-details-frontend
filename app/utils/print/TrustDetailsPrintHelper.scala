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
import controllers.maintain.routes._
import models.UserAnswers
import pages.{BusinessRelationshipYesNoPage, TrustEEAYesNoPage, TrustOwnUKLandOrPropertyPage}
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

class TrustDetailsPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) {

  def apply(userAnswers: UserAnswers)(implicit messages: Messages): AnswerSection = {

    val bound = answerRowConverter.bind(userAnswers)

    val answerRows: Seq[AnswerRow] = Seq(
      bound.yesNoQuestion(TrustEEAYesNoPage, "trustEEAYesNo", Some(TrustEEAYesNoController.onPageLoad().url)),
      bound.yesNoQuestion(TrustOwnUKLandOrPropertyPage, "trustOwnUKLandOrProperty", Some(TrustOwnUKLandOrPropertyController.onPageLoad().url)),
      bound.yesNoQuestion(BusinessRelationshipYesNoPage, "businessRelationshipYesNo", Some(BusinessRelationshipYesNoController.onPageLoad().url))
    ).flatten

    AnswerSection(None, answerRows)

  }
}
