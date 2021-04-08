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
import navigation.Navigator
import pages.TrustEEAYesNoPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.maintain.TrustEEAYesNoView

import scala.concurrent.{ExecutionContext, Future}

class TrustEEAYesNoController @Inject()(
                                              override val messagesApi: MessagesApi,
                                              repository: PlaybackRepository,
                                              yesNoFormProvider: YesNoFormProvider,
                                              navigator: Navigator,
                                              actions: StandardActionSets,
                                              val controllerComponents: MessagesControllerComponents,
                                              view: TrustEEAYesNoView
                                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = yesNoFormProvider.withPrefix("trustEEAYesNo")

  def onPageLoad(): Action[AnyContent] = actions.identifiedUserWithData {
    implicit request =>

      val preparedForm = request.userAnswers.get(TrustEEAYesNoPage) match {
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

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TrustEEAYesNoPage, value))
            _              <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(TrustEEAYesNoPage,updatedAnswers))
        }
      )
  }
}
