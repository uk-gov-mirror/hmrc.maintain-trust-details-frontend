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

package extractors

import base.SpecBase
import generators.ModelGenerators
import models.DeedOfVariation._
import models.TypeOfTrust._
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.maintain._

import java.time.LocalDate

class TrustDetailsExtractorSpec extends SpecBase with ScalaCheckPropertyChecks with ModelGenerators {

  private val extractor = injector.instanceOf[TrustDetailsExtractor]
  private val startDate = LocalDate.parse("2021-01-01")

  "TrustDetailsExtractor" when {

    "trustUKResident undefined and residentialStatus defined (i.e. 4mld data)" must {
      "extract if uk resident from residentialStatus" in {

        val trustDetails = TrustDetailsType(
          startDate = startDate,
          lawCountry = None,
          administrationCountry = None,
          residentialStatus = Some(ResidentialStatusType(Some(UkType(scottishLaw = true, None)), None)),
          trustUKProperty = None,
          trustRecorded = None,
          trustUKRelation = None,
          trustUKResident = None,
          typeOfTrust = None,
          deedOfVariation = None,
          interVivos = None,
          efrbsStartDate = None
        )

        val result = extractor(emptyUserAnswers, trustDetails).success.value

        result.get(TrustResidentInUkPage).get mustBe true
      }

      "extract if non-uk resident from residentialStatus" in {

        val trustDetails = TrustDetailsType(
          startDate = startDate,
          lawCountry = None,
          administrationCountry = None,
          residentialStatus = Some(ResidentialStatusType(None, Some(NonUKType(sch5atcgga92 = true, None, None, None)))),
          trustUKProperty = None,
          trustRecorded = None,
          trustUKRelation = None,
          trustUKResident = None,
          typeOfTrust = None,
          deedOfVariation = None,
          interVivos = None,
          efrbsStartDate = None
        )

        val result = extractor(emptyUserAnswers, trustDetails).success.value

        result.get(TrustResidentInUkPage).get mustBe false
      }
    }

    "trustUKResident and residentialStatus defined (i.e. 5mld taxable data)" must {
      "extract if uk resident from trustUKResident" in {

        val trustDetails = TrustDetailsType(
          startDate = startDate,
          lawCountry = None,
          administrationCountry = None,
          residentialStatus = Some(ResidentialStatusType(Some(UkType(scottishLaw = true, None)), None)),
          trustUKProperty = Some(true),
          trustRecorded = Some(true),
          trustUKRelation = None,
          trustUKResident = Some(true),
          typeOfTrust = None,
          deedOfVariation = None,
          interVivos = None,
          efrbsStartDate = None
        )

        val result = extractor(emptyUserAnswers, trustDetails).success.value

        result.get(OwnsUkLandOrPropertyPage).get mustBe true
        result.get(RecordedOnEeaRegisterPage).get mustBe true
        result.get(BusinessRelationshipInUkPage) mustBe None
        result.get(TrustResidentInUkPage).get mustBe true
      }

      "extract if non-uk resident from trustUKResident" in {

        val trustDetails = TrustDetailsType(
          startDate = startDate,
          lawCountry = None,
          administrationCountry = None,
          residentialStatus = Some(ResidentialStatusType(None, Some(NonUKType(sch5atcgga92 = true, None, None, None)))),
          trustUKProperty = Some(false),
          trustRecorded = Some(false),
          trustUKRelation = Some(true),
          trustUKResident = Some(false),
          typeOfTrust = None,
          deedOfVariation = None,
          interVivos = None,
          efrbsStartDate = None
        )

        val result = extractor(emptyUserAnswers, trustDetails).success.value

        result.get(OwnsUkLandOrPropertyPage).get mustBe false
        result.get(RecordedOnEeaRegisterPage).get mustBe false
        result.get(BusinessRelationshipInUkPage).get mustBe true
        result.get(TrustResidentInUkPage).get mustBe false
      }
    }

    "trustUKResident defined and residentialStatus undefined (i.e. 5mld non-taxable data)" must {
      "extract if uk resident from trustUKResident" in {

        val trustDetails = TrustDetailsType(
          startDate = startDate,
          lawCountry = None,
          administrationCountry = None,
          residentialStatus = None,
          trustUKProperty = Some(true),
          trustRecorded = Some(true),
          trustUKRelation = None,
          trustUKResident = Some(true),
          typeOfTrust = None,
          deedOfVariation = None,
          interVivos = None,
          efrbsStartDate = None
        )

        val result = extractor(emptyUserAnswers, trustDetails).success.value

        result.get(OwnsUkLandOrPropertyPage).get mustBe true
        result.get(RecordedOnEeaRegisterPage).get mustBe true
        result.get(BusinessRelationshipInUkPage) mustBe None
        result.get(TrustResidentInUkPage).get mustBe true
      }

      "extract if non-uk resident from trustUKResident" in {

        val trustDetails = TrustDetailsType(
          startDate = startDate,
          lawCountry = None,
          administrationCountry = None,
          residentialStatus = None,
          trustUKProperty = Some(false),
          trustRecorded = Some(false),
          trustUKRelation = Some(true),
          trustUKResident = Some(false),
          typeOfTrust = None,
          deedOfVariation = None,
          interVivos = None,
          efrbsStartDate = None
        )

        val result = extractor(emptyUserAnswers, trustDetails).success.value

        result.get(OwnsUkLandOrPropertyPage).get mustBe false
        result.get(RecordedOnEeaRegisterPage).get mustBe false
        result.get(BusinessRelationshipInUkPage).get mustBe true
        result.get(TrustResidentInUkPage).get mustBe false
      }
    }

    "trustUKResident and residentialStatus undefined (i.e. bad data)" must {
      "return failure" in {

        val trustDetails = TrustDetailsType(
          startDate = startDate,
          lawCountry = None,
          administrationCountry = None,
          residentialStatus = None,
          trustUKProperty = Some(false),
          trustRecorded = Some(false),
          trustUKRelation = Some(true),
          trustUKResident = None,
          typeOfTrust = None,
          deedOfVariation = None,
          interVivos = None,
          efrbsStartDate = None
        )

        val result = extractor(emptyUserAnswers, trustDetails)

        result mustBe 'failure
      }
    }

    "extract trust type answers" when {

      "WillTrustOrIntestacyTrust" in {

        val trustDetails = TrustDetailsType(
          startDate = startDate,
          lawCountry = None,
          administrationCountry = None,
          residentialStatus = None,
          trustUKProperty = Some(true),
          trustRecorded = Some(true),
          trustUKRelation = None,
          trustUKResident = Some(true),
          typeOfTrust = Some(WillTrustOrIntestacyTrust),
          deedOfVariation = None,
          interVivos = None,
          efrbsStartDate = None
        )

        val result = extractor(emptyUserAnswers, trustDetails).success.value

        result.get(SetUpAfterSettlorDiedPage).get mustBe true
        result.get(TypeOfTrustPage) mustBe None
        result.get(SetUpInAdditionToWillTrustPage) mustBe None
        result.get(WhyDeedOfVariationCreatedPage) mustBe None
        result.get(HoldoverReliefClaimedPage) mustBe None
        result.get(EfrbsYesNoPage) mustBe None
        result.get(EfrbsStartDatePage) mustBe None
      }

      "DeedOfVariationTrustOrFamilyArrangement" when {

        "trust set up in addition to will trust" in {

          val trustDetails = TrustDetailsType(
            startDate = startDate,
            lawCountry = None,
            administrationCountry = None,
            residentialStatus = None,
            trustUKProperty = Some(true),
            trustRecorded = Some(true),
            trustUKRelation = None,
            trustUKResident = Some(true),
            typeOfTrust = Some(DeedOfVariationTrustOrFamilyArrangement),
            deedOfVariation = Some(AdditionToWillTrust),
            interVivos = None,
            efrbsStartDate = None
          )

          val result = extractor(emptyUserAnswers, trustDetails).success.value

          result.get(SetUpAfterSettlorDiedPage).get mustBe false
          result.get(TypeOfTrustPage).get mustBe DeedOfVariationTrustOrFamilyArrangement
          result.get(SetUpInAdditionToWillTrustPage).get mustBe true
          result.get(WhyDeedOfVariationCreatedPage) mustBe None
          result.get(HoldoverReliefClaimedPage) mustBe None
          result.get(EfrbsYesNoPage) mustBe None
          result.get(EfrbsStartDatePage) mustBe None
        }

        "trust not set up in addition to will trust" in {

          forAll(arbitrary[DeedOfVariation].suchThat(_ != AdditionToWillTrust)) {
            deedOfVariation =>

              val trustDetails = TrustDetailsType(
                startDate = startDate,
                lawCountry = None,
                administrationCountry = None,
                residentialStatus = None,
                trustUKProperty = Some(true),
                trustRecorded = Some(true),
                trustUKRelation = None,
                trustUKResident = Some(true),
                typeOfTrust = Some(DeedOfVariationTrustOrFamilyArrangement),
                deedOfVariation = Some(deedOfVariation),
                interVivos = None,
                efrbsStartDate = None
              )

              val result = extractor(emptyUserAnswers, trustDetails).success.value

              result.get(SetUpAfterSettlorDiedPage).get mustBe false
              result.get(TypeOfTrustPage).get mustBe DeedOfVariationTrustOrFamilyArrangement
              result.get(SetUpInAdditionToWillTrustPage).get mustBe false
              result.get(WhyDeedOfVariationCreatedPage).get mustBe deedOfVariation
              result.get(HoldoverReliefClaimedPage) mustBe None
              result.get(EfrbsYesNoPage) mustBe None
              result.get(EfrbsStartDatePage) mustBe None
          }
        }
      }

      "InterVivosSettlement" in {

        forAll(arbitrary[Boolean]) {
          interVivos =>

            val trustDetails = TrustDetailsType(
              startDate = startDate,
              lawCountry = None,
              administrationCountry = None,
              residentialStatus = None,
              trustUKProperty = Some(true),
              trustRecorded = Some(true),
              trustUKRelation = None,
              trustUKResident = Some(true),
              typeOfTrust = Some(InterVivosSettlement),
              deedOfVariation = None,
              interVivos = Some(interVivos),
              efrbsStartDate = None
            )

            val result = extractor(emptyUserAnswers, trustDetails).success.value

            result.get(SetUpAfterSettlorDiedPage).get mustBe false
            result.get(TypeOfTrustPage).get mustBe InterVivosSettlement
            result.get(SetUpInAdditionToWillTrustPage) mustBe None
            result.get(WhyDeedOfVariationCreatedPage) mustBe None
            result.get(HoldoverReliefClaimedPage).get mustBe interVivos
            result.get(EfrbsYesNoPage) mustBe None
            result.get(EfrbsStartDatePage) mustBe None
        }
      }

      "EmploymentRelated" when {

        "it's an employer-financed retirement benefits scheme (EFRBS)" in {

          forAll(arbitrary[LocalDate]) {
            efrbsStartDate =>

              val trustDetails = TrustDetailsType(
                startDate = startDate,
                lawCountry = None,
                administrationCountry = None,
                residentialStatus = None,
                trustUKProperty = Some(true),
                trustRecorded = Some(true),
                trustUKRelation = None,
                trustUKResident = Some(true),
                typeOfTrust = Some(EmploymentRelated),
                deedOfVariation = None,
                interVivos = None,
                efrbsStartDate = Some(efrbsStartDate)
              )

              val result = extractor(emptyUserAnswers, trustDetails).success.value

              result.get(SetUpAfterSettlorDiedPage).get mustBe false
              result.get(TypeOfTrustPage).get mustBe EmploymentRelated
              result.get(SetUpInAdditionToWillTrustPage) mustBe None
              result.get(WhyDeedOfVariationCreatedPage) mustBe None
              result.get(HoldoverReliefClaimedPage) mustBe None
              result.get(EfrbsYesNoPage).get mustBe true
              result.get(EfrbsStartDatePage).get mustBe efrbsStartDate
          }
        }

        "it's not an employer-financed retirement benefits scheme (EFRBS)" in {

          val trustDetails = TrustDetailsType(
            startDate = startDate,
            lawCountry = None,
            administrationCountry = None,
            residentialStatus = None,
            trustUKProperty = Some(true),
            trustRecorded = Some(true),
            trustUKRelation = None,
            trustUKResident = Some(true),
            typeOfTrust = Some(EmploymentRelated),
            deedOfVariation = None,
            interVivos = None,
            efrbsStartDate = None
          )

          val result = extractor(emptyUserAnswers, trustDetails).success.value

          result.get(SetUpAfterSettlorDiedPage).get mustBe false
          result.get(TypeOfTrustPage).get mustBe EmploymentRelated
          result.get(SetUpInAdditionToWillTrustPage) mustBe None
          result.get(WhyDeedOfVariationCreatedPage) mustBe None
          result.get(HoldoverReliefClaimedPage) mustBe None
          result.get(EfrbsYesNoPage).get mustBe false
          result.get(EfrbsStartDatePage) mustBe None
        }
      }

      "HeritageMaintenanceFund" in {

        val trustDetails = TrustDetailsType(
          startDate = startDate,
          lawCountry = None,
          administrationCountry = None,
          residentialStatus = None,
          trustUKProperty = Some(true),
          trustRecorded = Some(true),
          trustUKRelation = None,
          trustUKResident = Some(true),
          typeOfTrust = Some(HeritageMaintenanceFund),
          deedOfVariation = None,
          interVivos = None,
          efrbsStartDate = None
        )

        val result = extractor(emptyUserAnswers, trustDetails).success.value

        result.get(SetUpAfterSettlorDiedPage).get mustBe false
        result.get(TypeOfTrustPage).get mustBe HeritageMaintenanceFund
        result.get(SetUpInAdditionToWillTrustPage) mustBe None
        result.get(WhyDeedOfVariationCreatedPage) mustBe None
        result.get(HoldoverReliefClaimedPage) mustBe None
        result.get(EfrbsYesNoPage) mustBe None
        result.get(EfrbsStartDatePage) mustBe None
      }

      "FlatManagementCompanyOrSinkingFund" in {

        val trustDetails = TrustDetailsType(
          startDate = startDate,
          lawCountry = None,
          administrationCountry = None,
          residentialStatus = None,
          trustUKProperty = Some(true),
          trustRecorded = Some(true),
          trustUKRelation = None,
          trustUKResident = Some(true),
          typeOfTrust = Some(FlatManagementCompanyOrSinkingFund),
          deedOfVariation = None,
          interVivos = None,
          efrbsStartDate = None
        )

        val result = extractor(emptyUserAnswers, trustDetails).success.value

        result.get(SetUpAfterSettlorDiedPage).get mustBe false
        result.get(TypeOfTrustPage).get mustBe FlatManagementCompanyOrSinkingFund
        result.get(SetUpInAdditionToWillTrustPage) mustBe None
        result.get(WhyDeedOfVariationCreatedPage) mustBe None
        result.get(HoldoverReliefClaimedPage) mustBe None
        result.get(EfrbsYesNoPage) mustBe None
        result.get(EfrbsStartDatePage) mustBe None
      }
    }
  }
}
