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

///*
// * Copyright 2021 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package controllers.maintain
//
//import java.time.LocalDate
//
//import base.SpecBase
//import connectors.TrustConnector
//import org.mockito.Matchers.any
//import org.mockito.Mockito.when
//import org.scalatest.concurrent.ScalaFutures
//import org.scalatestplus.mockito.MockitoSugar
//import pages.{BusinessRelationshipYesNoPage, TrustEEAYesNoPage, TrustOwnUKLandOrPropertyPage}
//import play.api.inject.bind
//import play.api.test.FakeRequest
//import play.api.test.Helpers._
//import uk.gov.hmrc.auth.core.AffinityGroup.Agent
//import uk.gov.hmrc.http.HttpResponse
//import views.html.maintain.CheckDetailsView
//
//import scala.concurrent.Future
//
//class CheckDetailsControllerSpec extends SpecBase with MockitoSugar with ScalaFutures {
//
//  private lazy val checkDetailsRoute = controllers.maintain.routes.CheckDetailsController.onPageLoad().url
//  private lazy val submitDetailsRoute = controllers.maintain.routes.CheckDetailsController.onSubmit().url
//  private lazy val onwardRoute = ???
//
//  private val userAnswers = emptyUserAnswers
//    .set(BusinessRelationshipYesNoPage, true).success.value
//    .set(TrustEEAYesNoPage, true).success.value
//    .set(TrustOwnUKLandOrPropertyPage, true).success.value
//
//  "CheckDetails Controller" must {
//
//    "return OK and the correct view for a GET" in {
//
//      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
//
//      val request = FakeRequest(GET, checkDetailsRoute)
//
//      val result = route(application, request).value
//
//      val view = application.injector.instanceOf[CheckDetailsView]
//      val printHelper = application.injector.instanceOf[OtherIndividualPrintHelper]
//      val answerSection = printHelper(userAnswers)
//
//      status(result) mustEqual OK
//
//      contentAsString(result) mustEqual
//        view(answerSection)(request, messages).toString
//    }
//
//    "redirect to the 'add a individual's page when submitted" in {
//
//      val mockTrustConnector = mock[TrustConnector]
//
//      val application =
//        applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
//          .overrides(bind[TrustConnector].toInstance(mockTrustConnector))
//          .build()
//
//      when(mockTrustConnector.addOtherIndividual(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
//
//      val request = FakeRequest(POST, submitDetailsRoute)
//
//      val result = route(application, request).value
//
//      status(result) mustEqual SEE_OTHER
//
//      redirectLocation(result).value mustEqual onwardRoute
//
//      application.stop()
//    }
//
//  }
//}
