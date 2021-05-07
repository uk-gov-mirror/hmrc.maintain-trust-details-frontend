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

package controllers.maintain

import controllers.actions.StandardActionSets
import forms.YesNoFormProvider
import javax.inject.Inject
import models.{TypeOfTrust, UserAnswers}
import navigation.Navigator
import pages.maintain.{SetUpAfterSettlorDiedPage, TypeOfTrustPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.maintain.SetUpAfterSettlorDiedView

import scala.concurrent.{ExecutionContext, Future}

class SetUpAfterSettlorDiedController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 yesNoFormProvider: YesNoFormProvider,
                                                 repository: PlaybackRepository,
                                                 navigator: Navigator,
                                                 actions: StandardActionSets,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 view: SetUpAfterSettlorDiedView
                                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[Boolean] = yesNoFormProvider.withPrefix("setUpAfterSettlorDied")

  def onPageLoad(): Action[AnyContent] = actions.identifiedUserWithData {
    implicit request =>

      val preparedForm = request.userAnswers.get(SetUpAfterSettlorDiedPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm))
  }

  def onSubmit(): Action[AnyContent] = actions.identifiedUserWithData.async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors))),

        hasSettlorDied => {
          for {
            answersWithSetUpValue <- Future.fromTry(request.userAnswers.set(SetUpAfterSettlorDiedPage, hasSettlorDied))
            updatedAnswers <- addDefaultTypeOfTrust(hasSettlorDied, answersWithSetUpValue)
            _ <- repository.set(updatedAnswers)
          } yield {
            Redirect(navigator.nextPage(SetUpAfterSettlorDiedPage, updatedAnswers))
          }
        }
      )
  }

  def addDefaultTypeOfTrust(hasSettlorDied: Boolean, userAnswers: UserAnswers): Future[UserAnswers] = {
    if (hasSettlorDied) {
      Future.fromTry(userAnswers.set(TypeOfTrustPage, TypeOfTrust.WillTrustOrIntestacyTrust))
    } else {
      Future.successful(userAnswers)
    }
  }
}
