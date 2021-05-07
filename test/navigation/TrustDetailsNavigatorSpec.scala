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

package navigation

import base.SpecBase
import models.TypeOfTrust
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.maintain.{BusinessRelationshipInUkPage, OwnsUkLandOrPropertyPage, RecordedOnEeaRegisterPage, SetUpAfterSettlorDiedPage, TrustResidentInUkPage, TypeOfTrustPage}

class TrustDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  private val navigator: TrustDetailsNavigator = injector.instanceOf[TrustDetailsNavigator]

  "TrustDetailsNavigator" when {

    "maintaining" must {

      "TrustOwnUKLandOrProperty page -> TrustEEAYesNo page" in {
        navigator.nextPage(OwnsUkLandOrPropertyPage, emptyUserAnswers)
          .mustBe(controllers.maintain.routes.RecordedOnEeaRegisterController.onPageLoad())
      }

      "TrustEEAYesNo page" when {
        "UK resident trust" must {
          "-> CYA page" in {
            val answers = emptyUserAnswers
              .set(TrustResidentInUkPage, true).success.value

            navigator.nextPage(RecordedOnEeaRegisterPage, answers)
              .mustBe(controllers.maintain.routes.CheckDetailsController.onPageLoad())
          }
        }
        "non-UK resident trust" must {
          "-> BusinessRelationshipYesNo page" in {
            val answers = emptyUserAnswers
              .set(TrustResidentInUkPage, false).success.value

            navigator.nextPage(RecordedOnEeaRegisterPage, answers)
              .mustBe(controllers.maintain.routes.BusinessRelationshipInUkController.onPageLoad())
          }
        }
      }

      "BusinessRelationshipYesNo page -> CYA page" in {
        navigator.nextPage(BusinessRelationshipInUkPage, emptyUserAnswers)
          .mustBe(controllers.maintain.routes.CheckDetailsController.onPageLoad())
      }


      "SetUpAfterSettlorDied page" when {
        "Yes -> Trustees in the UK page" in {
          val answers = emptyUserAnswers
            .set(SetUpAfterSettlorDiedPage, true).success.value

          navigator.nextPage(SetUpAfterSettlorDiedPage, answers)
            .mustBe(controllers.maintain.routes.WhereTrusteesBasedController.onPageLoad())
        }

        "No -> TypeOfTrust page" in {
          val answers = emptyUserAnswers
            .set(SetUpAfterSettlorDiedPage, false).success.value

          navigator.nextPage(SetUpAfterSettlorDiedPage, answers)
            .mustBe(controllers.maintain.routes.TypeOfTrustController.onPageLoad())
        }

        "No Data -> Session Expired page" in {
          navigator.nextPage(SetUpAfterSettlorDiedPage, emptyUserAnswers)
            .mustBe(controllers.routes.SessionExpiredController.onPageLoad())
        }
      }

      //ToDo Change to Trustees in the UK Controller
      "TypeOfTrust page" when {

        "DeedOfVariation -> DeedOfVariation page" in {
          val answers = emptyUserAnswers
            .set(TypeOfTrustPage, TypeOfTrust.DeedOfVariationTrustOrFamilyArrangement).success.value

          navigator.nextPage(TypeOfTrustPage, answers)
            .mustBe(controllers.routes.FeatureNotAvailableController.onPageLoad())
        }

        "WillTrust -> Feature Not Available page" in {
          val answers = emptyUserAnswers
            .set(TypeOfTrustPage, TypeOfTrust.WillTrustOrIntestacyTrust).success.value

          navigator.nextPage(TypeOfTrustPage, answers)
            .mustBe(controllers.routes.FeatureNotAvailableController.onPageLoad())
        }

        "Heritage -> Heritage page" in {
          val answers = emptyUserAnswers
            .set(TypeOfTrustPage, TypeOfTrust.HeritageMaintenanceFund).success.value

          navigator.nextPage(TypeOfTrustPage, answers)
            .mustBe(controllers.routes.FeatureNotAvailableController.onPageLoad())
        }

        "Flat Management -> Flat Management page" in {
          val answers = emptyUserAnswers
            .set(TypeOfTrustPage, TypeOfTrust.FlatManagementCompanyOrSinkingFund).success.value

          navigator.nextPage(TypeOfTrustPage, answers)
            .mustBe(controllers.routes.FeatureNotAvailableController.onPageLoad())
        }

        "EmploymentRelated -> EmploymentRelated page" in {
          val answers = emptyUserAnswers
            .set(TypeOfTrustPage, TypeOfTrust.EmploymentRelated).success.value

          navigator.nextPage(TypeOfTrustPage, answers)
            .mustBe(controllers.routes.FeatureNotAvailableController.onPageLoad())
        }

        "InterVivos -> InterVivos page" in {
          val answers = emptyUserAnswers
            .set(TypeOfTrustPage, TypeOfTrust.InterVivosSettlement).success.value

          navigator.nextPage(TypeOfTrustPage, answers)
            .mustBe(controllers.routes.FeatureNotAvailableController.onPageLoad())
        }

        "No Data -> Session Expired page" in {
          navigator.nextPage(TypeOfTrustPage, emptyUserAnswers)
            .mustBe(controllers.routes.SessionExpiredController.onPageLoad())
        }
      }
    }
  }
}
