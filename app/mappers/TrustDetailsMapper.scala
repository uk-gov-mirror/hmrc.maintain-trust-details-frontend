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

package mappers

import models.Constants.GB
import models.DeedOfVariation._
import models.TrusteesBased._
import models.TypeOfTrust._
import models._
import pages.maintain._
import play.api.Logging
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsResult, JsSuccess, Reads}

import java.time.LocalDate

class TrustDetailsMapper extends Logging {

  def apply(userAnswers: UserAnswers): JsResult[TrustDetails] = {

    if (userAnswers.migratingFromNonTaxableToTaxable) {
      userAnswers.data.validate[MigratingTrustDetails](migratingTrustDetailsReads)
    } else {
      userAnswers.data.validate[NonMigratingTrustDetails](nonMigratingTrustDetailsReads)
    }
  }

  private lazy val migratingTrustDetailsReads: Reads[MigratingTrustDetails] = {

    lazy val administrationCountryReads: Reads[String] = {
      AdministrationCountryPage.path.readNullable[String].flatMap {
        case Some(value) => Reads(_ => JsSuccess(value))
        case None => Reads(_ => JsSuccess(GB))
      }
    }

    lazy val residentialStatusReads: Reads[ResidentialStatusType] = {

      def combineReads(ukReads: Reads[Option[UkType]], nonUkReads: Reads[Option[NonUKType]]): Reads[ResidentialStatusType] = (
        ukReads and
          nonUkReads
        )(ResidentialStatusType.apply _)


      lazy val ukReads: Reads[ResidentialStatusType] = {
        lazy val reads: Reads[Option[UkType]] = (
          CreatedUnderScotsLawPage.path.read[Boolean] and
            PreviouslyResidentOffshoreCountryPage.path.readNullable[String]
          )(UkType.apply _).map(Some(_))

        combineReads(reads, Reads(_ => JsSuccess(None: Option[NonUKType])))
      }

      lazy val nonUkReads: Reads[ResidentialStatusType] = {
        lazy val reads: Reads[Option[NonUKType]] = (
          SettlorBenefitsFromAssetsPage.path.read[Boolean] and
            ForPurposeOfSection218Page.path.readNullable[Boolean] and
            AgentCreatedTrustPage.path.readNullable[Boolean] and
            Reads(_ => JsSuccess(None))
          )(NonUKType.apply _).map(Some(_))

        combineReads(Reads(_ => JsSuccess(None: Option[UkType])), reads)
      }

      basedInUkReads[ResidentialStatusType](ukReads, nonUkReads)
    }

    lazy val trustUkResidentReads: Reads[Boolean] = {
      basedInUkReads[Boolean](
        ukReads = Reads(_ => JsSuccess(true)),
        nonUkReads = Reads(_ => JsSuccess(false))
      )
    }

    def basedInUkReads[T](ukReads: Reads[T], nonUkReads: Reads[T]): Reads[T] = {
      WhereTrusteesBasedPage.path.read[TrusteesBased].flatMap {
        case AllTrusteesUkBased => ukReads
        case NoTrusteesUkBased => nonUkReads
        case InternationalAndUkBasedTrustees =>
          SettlorsUkBasedPage.path.read[Boolean].flatMap {
            case true => ukReads
            case false => nonUkReads
          }
      }
    }

    lazy val typeOfTrustReads: Reads[TypeOfTrust] = {
      TypeOfTrustPage.path.readNullable[TypeOfTrust].flatMap {
        case Some(DeedOfVariationTrustOrFamilyArrangement) => SetUpInAdditionToWillTrustPage.path.read[Boolean].flatMap {
          case true => Reads(_ => JsSuccess(WillTrustOrIntestacyTrust))
          case false => Reads(_ => JsSuccess(DeedOfVariationTrustOrFamilyArrangement))
        }
        case Some(value) => Reads(_ => JsSuccess(value))
        case None => Reads(_ => JsSuccess(WillTrustOrIntestacyTrust))
      }
    }

    lazy val deedOfVariationReads: Reads[Option[DeedOfVariation]] = {
      SetUpInAdditionToWillTrustPage.path.readNullable[Boolean].flatMap {
        case Some(true) => Reads(_ => JsSuccess(Some(AdditionToWillTrust)))
        case Some(false) => WhyDeedOfVariationCreatedPage.path.read[DeedOfVariation].map(Some(_))
        case None => Reads(_ => JsSuccess(None))
      }
    }

    (
      GoverningCountryPage.path.readNullable[String] and
      administrationCountryReads and
      residentialStatusReads and
      BusinessRelationshipInUkPage.path.readNullable[Boolean] and
      trustUkResidentReads and
      typeOfTrustReads and
      deedOfVariationReads and
      HoldoverReliefClaimedPage.path.readNullable[Boolean] and
      EfrbsStartDatePage.path.readNullable[LocalDate]
    )(MigratingTrustDetails.apply _)
  }

  private lazy val nonMigratingTrustDetailsReads: Reads[NonMigratingTrustDetails] = (
    OwnsUkLandOrPropertyPage.path.read[Boolean] and
      RecordedOnEeaRegisterPage.path.read[Boolean] and
      BusinessRelationshipInUkPage.path.readNullable[Boolean] and
      TrustResidentInUkPage.path.read[Boolean]
    )(NonMigratingTrustDetails.apply _)

}
