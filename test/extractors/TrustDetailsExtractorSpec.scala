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
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import java.time.LocalDate

class TrustDetailsExtractorSpec extends SpecBase with ScalaCheckPropertyChecks {

  private val extractor = injector.instanceOf[TrustDetailsExtractor]
  private val startDate = LocalDate.parse("2021-01-01")

  "TrustDetailsExtractor" must {

    "extract trustUKProperty, trustRecorded and trustUKRelation from TrustDetailsType to user answers" in {

      forAll(arbitrary[Option[Boolean]], arbitrary[Option[Boolean]], arbitrary[Option[Boolean]], arbitrary[Option[Boolean]]) {
        (trustUKProperty, trustRecorded, trustUKRelation, trustUKResident) =>
          val trustDetails = TrustDetailsType(
            startDate = startDate,
            lawCountry = None,
            administrationCountry = None,
            residentialStatus = None,
            trustUKProperty = trustUKProperty,
            trustRecorded = trustRecorded,
            trustUKRelation = trustUKRelation,
            trustUKResident = trustUKResident
          )

          val result = extractor(emptyUserAnswers, trustDetails).success.value

          result.get(TrustOwnUKLandOrPropertyPage) mustBe trustUKProperty
          result.get(TrustEEAYesNoPage) mustBe trustRecorded
          result.get(BusinessRelationshipYesNoPage) mustBe trustUKRelation
          result.get(TrustUKResidentPage) mustBe trustUKResident
      }
    }

    "extract if uk resident from residentialStatus when 4mld" in {

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

      result.get(TrustUKResidentPage) mustBe true
    }

    "extract if non-uk resident from residentialStatus when 4mld" in {

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

      result.get(TrustUKResidentPage) mustBe false
    }

  }
}
