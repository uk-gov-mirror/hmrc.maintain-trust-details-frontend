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

package generators

import models.DeedOfVariation._
import models.TrusteesBased._
import models.TypeOfTrust._
import models.http.TaxableMigrationFlag
import models.{DeedOfVariation, TrusteesBased, TypeOfTrust}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

import java.time.{Instant, LocalDate, ZoneOffset}

trait ModelGenerators {

  implicit lazy val arbitraryTaxableMigrationFlag: Arbitrary[TaxableMigrationFlag] = {
    Arbitrary {
      for {
        value <- arbitrary[Option[Boolean]]
      } yield {
        TaxableMigrationFlag(value)
      }
    }
  }

  implicit lazy val arbitraryTypeOfTrust: Arbitrary[TypeOfTrust] = {
    Arbitrary {
      Gen.oneOf(
        WillTrustOrIntestacyTrust,
        DeedOfVariationTrustOrFamilyArrangement,
        InterVivosSettlement,
        EmploymentRelated,
        HeritageMaintenanceFund,
        FlatManagementCompanyOrSinkingFund
      )
    }
  }

  implicit lazy val arbitraryDeedOfVariation: Arbitrary[DeedOfVariation] = {
    Arbitrary {
      Gen.oneOf(
        PreviouslyAbsoluteInterestUnderWill,
        ReplacedWillTrust,
        AdditionToWillTrust
      )
    }
  }

  implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = {
    Arbitrary {
      for {
        year <- Gen.choose(min = 1500, max = 2099)
        month <- Gen.choose(1, 12)
        day <- Gen.choose(
          min = 1,
          max = month match {
            case 2 if year % 4 == 0 => 29
            case 2 => 28
            case 4 | 6 | 9 | 11 => 30
            case _ => 31
          }
        )
      } yield {
        LocalDate.of(year, month, day)
      }
    }
  }

  implicit lazy val arbitraryTrusteesBased: Arbitrary[TrusteesBased] = {
    Arbitrary {
      Gen.oneOf(
        AllTrusteesUkBased,
        InternationalAndUkBasedTrustees,
        NoTrusteesUkBased
      )
    }
  }

  def datesBetween(min: LocalDate, max: LocalDate): Gen[LocalDate] = {

    def toMillis(date: LocalDate): Long =
      date.atStartOfDay.atZone(ZoneOffset.UTC).toInstant.toEpochMilli

    Gen.choose(toMillis(min), toMillis(max)).map {
      millis =>
        Instant.ofEpochMilli(millis).atOffset(ZoneOffset.UTC).toLocalDate
    }
  }

}
