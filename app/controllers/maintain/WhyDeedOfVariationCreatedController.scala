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

package controllers.maintain

import controllers.actions.StandardActionSets
import forms.EnumFormProvider
import models.DeedOfVariation
import navigation.Navigator
import pages.maintain.WhyDeedOfVariationCreatedPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.maintain.WhyDeedOfVariationCreatedView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class WhyDeedOfVariationCreatedController @Inject()(
                                                     override val messagesApi: MessagesApi,
                                                     standardActionSets: StandardActionSets,
                                                     repository: PlaybackRepository,
                                                     navigator: Navigator,
                                                     formProvider: EnumFormProvider,
                                                     val controllerComponents: MessagesControllerComponents,
                                                     view: WhyDeedOfVariationCreatedView
                                                   )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[DeedOfVariation] = formProvider("whyDeedOfVariationCreated")

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForIdentifier {
    implicit request =>
      val preparedForm = request.userAnswers.get(WhyDeedOfVariationCreatedPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm))
  }

  def onSubmit(): Action[AnyContent] = standardActionSets.verifiedForIdentifier.async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhyDeedOfVariationCreatedPage, value))
            _              <- repository.set(updatedAnswers)
          } yield {
            Redirect(navigator.nextPage(WhyDeedOfVariationCreatedPage, updatedAnswers))
          }
        }
      )
  }
}
