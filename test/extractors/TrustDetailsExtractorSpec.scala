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
import models.{NonUKType, ResidentialStatusType, TrustDetailsType, UkType}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.maintain.{BusinessRelationshipYesNoPage, TrustEEAYesNoPage, TrustOwnUKLandOrPropertyPage, TrustUKResidentPage}

import java.time.LocalDate

class TrustDetailsExtractorSpec extends SpecBase with ScalaCheckPropertyChecks {

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
          trustUKResident = None
        )

        val result = extractor(emptyUserAnswers, trustDetails).success.value

        result.get(TrustUKResidentPage).get mustBe true
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
          trustUKResident = None
        )

        val result = extractor(emptyUserAnswers, trustDetails).success.value

        result.get(TrustUKResidentPage).get mustBe false
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
          trustUKResident = Some(true)
        )

        val result = extractor(emptyUserAnswers, trustDetails).success.value

        result.get(TrustOwnUKLandOrPropertyPage).get mustBe true
        result.get(TrustEEAYesNoPage).get mustBe true
        result.get(BusinessRelationshipYesNoPage) mustBe None
        result.get(TrustUKResidentPage).get mustBe true
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
          trustUKResident = Some(false)
        )

        val result = extractor(emptyUserAnswers, trustDetails).success.value

        result.get(TrustOwnUKLandOrPropertyPage).get mustBe false
        result.get(TrustEEAYesNoPage).get mustBe false
        result.get(BusinessRelationshipYesNoPage).get mustBe true
        result.get(TrustUKResidentPage).get mustBe false
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
          trustUKResident = Some(true)
        )

        val result = extractor(emptyUserAnswers, trustDetails).success.value

        result.get(TrustOwnUKLandOrPropertyPage).get mustBe true
        result.get(TrustEEAYesNoPage).get mustBe true
        result.get(BusinessRelationshipYesNoPage) mustBe None
        result.get(TrustUKResidentPage).get mustBe true
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
          trustUKResident = Some(false)
        )

        val result = extractor(emptyUserAnswers, trustDetails).success.value

        result.get(TrustOwnUKLandOrPropertyPage).get mustBe false
        result.get(TrustEEAYesNoPage).get mustBe false
        result.get(BusinessRelationshipYesNoPage).get mustBe true
        result.get(TrustUKResidentPage).get mustBe false
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
          trustUKResident = None
        )

        val result = extractor(emptyUserAnswers, trustDetails)

        result mustBe 'failure
      }
    }
  }
}
