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

import base.SpecBase
import models.{NonUKType, ResidentialStatusType, TrustDetailsType, UkType}
import pages.maintain.{AnswersCompletedPage, BusinessRelationshipInUkPage, OwnsUkLandOrPropertyPage, RecordedOnEeaRegisterPage}

import java.time.LocalDate

class UserAnswersStatusSpec extends SpecBase {

  private val userAnswersStatus = injector.instanceOf[UserAnswersStatus]

  private val startDate = LocalDate.parse("2020-01-01")

  "UserAnswersStatus" when {

    ".areAnswersSubmittable" when {

      "not doing non-tax-to-tax migration" must {

        val baseAnswers = emptyUserAnswers.copy(migratingFromNonTaxableToTaxable = false)

        "return true" when {

          "taxable" when {

            "uk resident" in {

              val userAnswers = baseAnswers
                .set(OwnsUkLandOrPropertyPage, true).success.value
                .set(RecordedOnEeaRegisterPage, true).success.value

              val trustDetails = TrustDetailsType(
                startDate = startDate,
                lawCountry = None,
                administrationCountry = None,
                residentialStatus = Some(ResidentialStatusType(Some(UkType(scottishLaw = true, None)), None)),
                trustUKProperty = None,
                trustRecorded = None,
                trustUKRelation = None,
                trustUKResident = Some(true),
                typeOfTrust = None,
                deedOfVariation = None,
                interVivos = None,
                efrbsStartDate = None
              )

              userAnswersStatus.areAnswersSubmittable(userAnswers, trustDetails) mustBe true
            }

            "non-uk resident" in {

              val userAnswers = baseAnswers
                .set(OwnsUkLandOrPropertyPage, true).success.value
                .set(RecordedOnEeaRegisterPage, true).success.value
                .set(BusinessRelationshipInUkPage, true).success.value

              val trustDetails = TrustDetailsType(
                startDate = startDate,
                lawCountry = None,
                administrationCountry = None,
                residentialStatus = Some(ResidentialStatusType(None, Some(NonUKType(sch5atcgga92 = true, None, None, None)))),
                trustUKProperty = None,
                trustRecorded = None,
                trustUKRelation = None,
                trustUKResident = Some(false),
                typeOfTrust = None,
                deedOfVariation = None,
                interVivos = None,
                efrbsStartDate = None
              )

              userAnswersStatus.areAnswersSubmittable(userAnswers, trustDetails) mustBe true
            }
          }

          "non-taxable" when {

            "uk resident" in {

              val userAnswers = baseAnswers
                .set(OwnsUkLandOrPropertyPage, true).success.value
                .set(RecordedOnEeaRegisterPage, true).success.value

              val trustDetails = TrustDetailsType(
                startDate = startDate,
                lawCountry = None,
                administrationCountry = None,
                residentialStatus = None,
                trustUKProperty = None,
                trustRecorded = None,
                trustUKRelation = None,
                trustUKResident = Some(true),
                typeOfTrust = None,
                deedOfVariation = None,
                interVivos = None,
                efrbsStartDate = None
              )

              userAnswersStatus.areAnswersSubmittable(userAnswers, trustDetails) mustBe true
            }

            "non-uk resident" in {

              val userAnswers = baseAnswers
                .set(OwnsUkLandOrPropertyPage, true).success.value
                .set(RecordedOnEeaRegisterPage, true).success.value
                .set(BusinessRelationshipInUkPage, true).success.value

              val trustDetails = TrustDetailsType(
                startDate = startDate,
                lawCountry = None,
                administrationCountry = None,
                residentialStatus = None,
                trustUKProperty = None,
                trustRecorded = None,
                trustUKRelation = None,
                trustUKResident = Some(false),
                typeOfTrust = None,
                deedOfVariation = None,
                interVivos = None,
                efrbsStartDate = None
              )

              userAnswersStatus.areAnswersSubmittable(userAnswers, trustDetails) mustBe true
            }
          }
        }

        "return false" when {

          "TrustOwnUKLandOrPropertyPage and TrustEEAYesNoPage not answered" in {

            val userAnswers = baseAnswers

            val trustDetails = TrustDetailsType(
              startDate = startDate,
              lawCountry = None,
              administrationCountry = None,
              residentialStatus = None,
              trustUKProperty = None,
              trustRecorded = None,
              trustUKRelation = None,
              trustUKResident = None,
              typeOfTrust = None,
              deedOfVariation = None,
              interVivos = None,
              efrbsStartDate = None
            )

            userAnswersStatus.areAnswersSubmittable(userAnswers, trustDetails) mustBe false
          }

          "BusinessRelationshipYesNoPage not answered and non-UK resident" when {

            val userAnswers = baseAnswers
              .set(OwnsUkLandOrPropertyPage, true).success.value
              .set(RecordedOnEeaRegisterPage, true).success.value

            "taxable" in {

              val trustDetails = TrustDetailsType(
                startDate = startDate,
                lawCountry = None,
                administrationCountry = None,
                residentialStatus = Some(ResidentialStatusType(None, Some(NonUKType(sch5atcgga92 = true, None, None, None)))),
                trustUKProperty = None,
                trustRecorded = None,
                trustUKRelation = None,
                trustUKResident = Some(false),
                typeOfTrust = None,
                deedOfVariation = None,
                interVivos = None,
                efrbsStartDate = None
              )

              userAnswersStatus.areAnswersSubmittable(userAnswers, trustDetails) mustBe false
            }

            "non-taxable" in {

              val trustDetails = TrustDetailsType(
                startDate = startDate,
                lawCountry = None,
                administrationCountry = None,
                residentialStatus = None,
                trustUKProperty = None,
                trustRecorded = None,
                trustUKRelation = None,
                trustUKResident = Some(false),
                typeOfTrust = None,
                deedOfVariation = None,
                interVivos = None,
                efrbsStartDate = None
              )

              userAnswersStatus.areAnswersSubmittable(userAnswers, trustDetails) mustBe false
            }
          }
        }
      }

      "doing non-tax-to-tax migration" must {

        val baseAnswers = emptyUserAnswers.copy(migratingFromNonTaxableToTaxable = true)

        val trustDetails = TrustDetailsType(
          startDate = startDate,
          lawCountry = None,
          administrationCountry = None,
          residentialStatus = None,
          trustUKProperty = None,
          trustRecorded = None,
          trustUKRelation = None,
          trustUKResident = None,
          typeOfTrust = None,
          deedOfVariation = None,
          interVivos = None,
          efrbsStartDate = None
        )

        "return true" when {
          "AnswersCompletedPage contains true" in {

            val userAnswers = baseAnswers
              .set(AnswersCompletedPage, true).success.value

            userAnswersStatus.areAnswersSubmittable(userAnswers, trustDetails) mustBe true
          }
        }

        "return false" when {

          "AnswersCompletedPage contains false" in {

            val userAnswers = baseAnswers
              .set(AnswersCompletedPage, false).success.value

            userAnswersStatus.areAnswersSubmittable(userAnswers, trustDetails) mustBe false
          }

          "AnswersCompletedPage is undefined" in {

            val userAnswers = baseAnswers

            userAnswersStatus.areAnswersSubmittable(userAnswers, trustDetails) mustBe false
          }
        }
      }
    }
  }
}
