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
import models.{DeedOfVariation, TypeOfTrust}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.maintain._

class TrustDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  private val navigator: TrustDetailsNavigator = injector.instanceOf[TrustDetailsNavigator]

  "TrustDetailsNavigator" when {

    "maintaining" must {

      val baseAnswers = emptyUserAnswers.copy(migratingFromNonTaxableToTaxable = false)

      "Owns UK land or property page -> Recorded on EEA register page" in {
        navigator.nextPage(OwnsUkLandOrPropertyPage, baseAnswers)
          .mustBe(controllers.maintain.routes.RecordedOnEeaRegisterController.onPageLoad())
      }

      "Recorded on EEA register page" when {
        "UK resident trust" must {
          "-> CYA page" in {
            val answers = baseAnswers
              .set(TrustResidentInUkPage, true).success.value

            navigator.nextPage(RecordedOnEeaRegisterPage, answers)
              .mustBe(controllers.maintain.routes.CheckDetailsController.onPageLoad())
          }
        }
        "non-UK resident trust" must {
          "-> Business relationship in UK page" in {
            val answers = baseAnswers
              .set(TrustResidentInUkPage, false).success.value

            navigator.nextPage(RecordedOnEeaRegisterPage, answers)
              .mustBe(controllers.maintain.routes.BusinessRelationshipInUkController.onPageLoad())
          }
        }
      }

      "Business relationship in UK page -> CYA page" in {
        navigator.nextPage(BusinessRelationshipInUkPage, baseAnswers)
          .mustBe(controllers.maintain.routes.CheckDetailsController.onPageLoad())
      }
    }

    "migrating" must {

      val baseAnswers = emptyUserAnswers.copy(migratingFromNonTaxableToTaxable = true)

      "General Admin in the Uk page" when {
        val page = AdministeredInUkPage

        "Yes" when {
          "registered with deceased settlor" must {
            "-> Set up after settlor died page" in {
              val answers = baseAnswers.copy(registeredWithDeceasedSettlor = true)
                .set(page, true).success.value

              navigator.nextPage(page, answers)
                .mustBe(controllers.maintain.routes.SetUpAfterSettlorDiedController.onPageLoad())
            }
          }

          "not registered with deceased settlor" must {
            "-> Type of trust page" in {
              val answers = baseAnswers.copy(registeredWithDeceasedSettlor = false)
                .set(page, true).success.value

              navigator.nextPage(page, answers)
                .mustBe(controllers.maintain.routes.TypeOfTrustController.onPageLoad())
            }
          }
        }

        "No -> What country is the trust administered in page" in {
          val answers = baseAnswers
            .set(page, false).success.value

          navigator.nextPage(page, answers)
            .mustBe(controllers.routes.FeatureNotAvailableController.onPageLoad())
        }

        "No Data -> Session Expired page" in {
          navigator.nextPage(page, baseAnswers)
            .mustBe(controllers.routes.SessionExpiredController.onPageLoad())
        }
      }

      "Set up after settlor died page" when {

        val page = SetUpAfterSettlorDiedPage

        "Yes -> Where trustees based page" in {
          val answers = baseAnswers
            .set(page, true).success.value

          navigator.nextPage(page, answers)
            .mustBe(controllers.maintain.routes.WhereTrusteesBasedController.onPageLoad())
        }

        "No" when {
          "registered with deceased settlor" must {
            "-> Where trustees based page" in {
              val answers = baseAnswers.copy(registeredWithDeceasedSettlor = true)
                .set(page, false).success.value

              navigator.nextPage(page, answers)
                .mustBe(controllers.maintain.routes.WhereTrusteesBasedController.onPageLoad())
            }
          }

          "not registered with deceased settlor" must {
            "-> Type of trust page" in {
              val answers = baseAnswers.copy(registeredWithDeceasedSettlor = false)
                .set(page, false).success.value

              navigator.nextPage(page, answers)
                .mustBe(controllers.maintain.routes.TypeOfTrustController.onPageLoad())
            }
          }
        }
      }

      "Type of trust page" when {

        val page = TypeOfTrustPage

        "DeedOfVariationTrustOrFamilyArrangement" when {
          "registered with deceased settlor" must {
            "-> Where trustees based page" in {
              val answers = baseAnswers.copy(registeredWithDeceasedSettlor = true)
                .set(page, TypeOfTrust.DeedOfVariationTrustOrFamilyArrangement).success.value

              navigator.nextPage(page, answers)
                .mustBe(controllers.maintain.routes.WhereTrusteesBasedController.onPageLoad())
            }
          }

          "not registered with deceased settlor" must {
            "-> Why was deed of variation created page" in {
              val answers = baseAnswers.copy(registeredWithDeceasedSettlor = false)
                .set(page, TypeOfTrust.DeedOfVariationTrustOrFamilyArrangement).success.value

              navigator.nextPage(page, answers)
                .mustBe(controllers.maintain.routes.WhyDeedOfVariationCreatedController.onPageLoad())
            }
          }
        }

        "HeritageMaintenanceFund -> Where trustees based page" in {
          val answers = baseAnswers
            .set(page, TypeOfTrust.HeritageMaintenanceFund).success.value

          navigator.nextPage(page, answers)
            .mustBe(controllers.maintain.routes.WhereTrusteesBasedController.onPageLoad())
        }

        "FlatManagementCompanyOrSinkingFund -> Where trustees based page" in {
          val answers = baseAnswers
            .set(page, TypeOfTrust.FlatManagementCompanyOrSinkingFund).success.value

          navigator.nextPage(page, answers)
            .mustBe(controllers.maintain.routes.WhereTrusteesBasedController.onPageLoad())
        }

        "EmploymentRelated -> EFRBS yes/no page" in {
          val answers = baseAnswers
            .set(page, TypeOfTrust.EmploymentRelated).success.value

          navigator.nextPage(page, answers)
            .mustBe(controllers.maintain.routes.EfrbsYesNoController.onPageLoad())
        }

        "InterVivosSettlement -> Holdover relief claimed page" in {
          val answers = baseAnswers
            .set(page, TypeOfTrust.InterVivosSettlement).success.value

          navigator.nextPage(page, answers)
            .mustBe(controllers.maintain.routes.HoldoverReliefClaimedController.onPageLoad())
        }

        "No Data -> Session Expired page" in {
          navigator.nextPage(page, baseAnswers)
            .mustBe(controllers.routes.SessionExpiredController.onPageLoad())
        }
      }

      "Why Deed of Variation Created page" when {

        val page = WhyDeedOfVariationCreatedPage

        "To replace a will trust -> Where trustees based page" in {
          val answers = baseAnswers
            .set(page, DeedOfVariation.ReplacedWillTrust).success.value

          navigator.nextPage(page, answers)
            .mustBe(controllers.maintain.routes.WhereTrusteesBasedController.onPageLoad())
        }

        "To replace an absolute interest over will -> Where trustees based page" in {
          val answers = baseAnswers
            .set(page, DeedOfVariation.PreviouslyAbsoluteInterestUnderWill).success.value

          navigator.nextPage(page, answers)
            .mustBe(controllers.maintain.routes.WhereTrusteesBasedController.onPageLoad())
        }
      }

      "Holdover relief claimed page -> Where trustees based page" in {
        navigator.nextPage(HoldoverReliefClaimedPage, baseAnswers)
          .mustBe(controllers.maintain.routes.WhereTrusteesBasedController.onPageLoad())
      }

      "EFRBS yes/no page" when {

        val page = EfrbsYesNoPage

        "Yes -> EFRBS start date page" in {
          val answers = baseAnswers
            .set(page, true).success.value

          navigator.nextPage(page, answers)
            .mustBe(controllers.maintain.routes.EfrbsStartDateController.onPageLoad())
        }

        "No -> Where trustees based page" in {
          val answers = baseAnswers
            .set(page, false).success.value

          navigator.nextPage(page, answers)
            .mustBe(controllers.maintain.routes.WhereTrusteesBasedController.onPageLoad())
        }

        "No Data -> Session Expired page" in {
          navigator.nextPage(page, baseAnswers)
            .mustBe(controllers.routes.SessionExpiredController.onPageLoad())
        }
      }

      "EFRBS start date page -> Where trustees based page" in {
        navigator.nextPage(EfrbsStartDatePage, baseAnswers)
          .mustBe(controllers.maintain.routes.WhereTrusteesBasedController.onPageLoad())
      }
    }
  }
}
