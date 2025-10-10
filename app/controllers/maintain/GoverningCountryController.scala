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
import forms.CountryFormProvider
import navigation.Navigator
import pages.maintain.GoverningCountryPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.CountryOptions
import views.html.maintain.GoverningCountryView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GoverningCountryController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            formProvider: CountryFormProvider,
                                            repository: PlaybackRepository,
                                            navigator: Navigator,
                                            actions: StandardActionSets,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: GoverningCountryView,
                                            countryOptions: CountryOptions
                                          )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[String] = formProvider.withPrefix("governingCountry")

  def onPageLoad(): Action[AnyContent] = actions.identifiedUserWithData {
    implicit request =>

      val preparedForm = request.userAnswers.get(GoverningCountryPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, countryOptions.nonUkOptions))
  }

  def onSubmit(): Action[AnyContent] = actions.identifiedUserWithData.async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, countryOptions.nonUkOptions))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(GoverningCountryPage, value))
            _ <- repository.set(updatedAnswers)
          } yield {
            Redirect(navigator.nextPage(GoverningCountryPage, updatedAnswers))
          }
        }
      )
  }
}
