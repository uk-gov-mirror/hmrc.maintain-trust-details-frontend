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
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsResult, JsSuccess, Reads}

import java.time.LocalDate

class TrustDetailsMapper {

  def areAnswersSubmittable(userAnswers: UserAnswers): Boolean = apply(userAnswers).isSuccess

  def apply(userAnswers: UserAnswers): JsResult[TrustDetails] = {

    if (userAnswers.migratingFromNonTaxableToTaxable) {
      userAnswers.data.validate[MigratingTrustDetails](migratingTrustDetailsReads(userAnswers.registeredWithDeceasedSettlor))
    } else {
      userAnswers.data.validate[NonMigratingTrustDetails](nonMigratingTrustDetailsReads)
    }
  }

  private def migratingTrustDetailsReads(registeredWithDeceasedSettlor: Boolean): Reads[MigratingTrustDetails] = {

    lazy val governingCountryReads: Reads[Option[String]] = {
      GovernedByUkLawPage.path.read[Boolean].flatMap {
        case true => Reads(_ => JsSuccess(None))
        case false => GoverningCountryPage.path.read[String].map(Some(_))
      }
    }

    lazy val administrationCountryReads: Reads[String] = {
      AdministeredInUkPage.path.read[Boolean].flatMap {
        case true => Reads(_ => JsSuccess(GB))
        case false => AdministrationCountryPage.path.read[String]
      }
    }

    lazy val residentialStatusReads: Reads[ResidentialStatusType] = {

      def combineReads(ukReads: Reads[Option[UkType]],
                       nonUkReads: Reads[Option[NonUKType]]): Reads[ResidentialStatusType] = (
        ukReads and
          nonUkReads
        )(ResidentialStatusType.apply _)


      lazy val ukReads: Reads[ResidentialStatusType] = {

        lazy val preOffShoreReads: Reads[Option[String]] = {
          PreviouslyResidentOffshorePage.path.read[Boolean].flatMap {
            case true => PreviouslyResidentOffshoreCountryPage.path.read[String].map(Some(_))
            case false => Reads(_ => JsSuccess(None))
          }
        }

        lazy val reads: Reads[Option[UkType]] = (
          CreatedUnderScotsLawPage.path.read[Boolean] and
            preOffShoreReads
          )(UkType.apply _).map(Some(_))

        combineReads(reads, Reads(_ => JsSuccess(None: Option[NonUKType])))
      }

      lazy val nonUkReads: Reads[ResidentialStatusType] = {

        lazy val s218ihta84Reads: Reads[Option[Boolean]] = {
          SettlorBenefitsFromAssetsPage.path.read[Boolean].flatMap {
            case true => Reads(_ => JsSuccess(None))
            case false => ForPurposeOfSection218Page.path.read[Boolean].map(Some(_))
          }
        }

        lazy val agentS218IHTA84Reads: Reads[Option[Boolean]] = {
          ForPurposeOfSection218Page.path.readNullable[Boolean].flatMap {
            case Some(true) => AgentCreatedTrustPage.path.read[Boolean].map(Some(_))
            case _ => Reads(_ => JsSuccess(None))
          }
        }

        lazy val reads: Reads[Option[NonUKType]] = (
          SettlorBenefitsFromAssetsPage.path.read[Boolean] and
            s218ihta84Reads and
            agentS218IHTA84Reads and
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
      if (registeredWithDeceasedSettlor) {
        Reads(_ => JsSuccess(WillTrustOrIntestacyTrust))
      } else {
        TypeOfTrustPage.path.read[TypeOfTrust]
      }
    }

    lazy val deedOfVariationReads: Reads[Option[DeedOfVariation]] = {
      SetUpAfterSettlorDiedPage.path.readNullable[Boolean].flatMap {
        case Some(false) if registeredWithDeceasedSettlor => Reads(_ => JsSuccess(Some(AdditionToWillTrust)))
        case None if !registeredWithDeceasedSettlor => TypeOfTrustPage.path.read[TypeOfTrust].flatMap {
          case DeedOfVariationTrustOrFamilyArrangement => WhyDeedOfVariationCreatedPage.path.read[DeedOfVariation].map(Some(_))
          case _ => Reads(_ => JsSuccess(None))
        }
        case _ => SetUpInAdditionToWillTrustPage.path.readNullable[Boolean].flatMap {
          case Some(true) => Reads(_ => JsSuccess(Some(AdditionToWillTrust)))
          case Some(false) => WhyDeedOfVariationCreatedPage.path.read[DeedOfVariation].map(Some(_))
          case None => Reads(_ => JsSuccess(None))
        }
      }
    }

    lazy val interVivosReads: Reads[Option[Boolean]] = {
      TypeOfTrustPage.path.readNullable[TypeOfTrust].flatMap {
        case Some(InterVivosSettlement) => HoldoverReliefClaimedPage.path.read[Boolean].map(Some(_))
        case _ => Reads(_ => JsSuccess(None))
      }
    }

    lazy val efrbsStartDateReads: Reads[Option[LocalDate]] = {
      TypeOfTrustPage.path.readNullable[TypeOfTrust].flatMap {
        case Some(EmploymentRelated) => EfrbsYesNoPage.path.read[Boolean].flatMap {
          case true => EfrbsStartDatePage.path.read[LocalDate].map(Some(_))
          case false => Reads(_ => JsSuccess(None))
        }
        case _ => Reads(_ => JsSuccess(None))
      }
    }

    (
      governingCountryReads and
        administrationCountryReads and
        residentialStatusReads and
        BusinessRelationshipInUkPage.path.readNullable[Boolean] and
        trustUkResidentReads and
        typeOfTrustReads and
        deedOfVariationReads and
        interVivosReads and
        efrbsStartDateReads
      )(MigratingTrustDetails.apply _)
  }

  private lazy val nonMigratingTrustDetailsReads: Reads[NonMigratingTrustDetails] = {

    lazy val businessRelationshipInUkReads: Reads[Option[Boolean]] = {
      TrustResidentInUkPage.path.read[Boolean].flatMap {
        case true => Reads(_ => JsSuccess(None))
        case false => BusinessRelationshipInUkPage.path.read[Boolean].map(Some(_))
      }
    }

    (
      OwnsUkLandOrPropertyPage.path.read[Boolean] and
        RecordedOnEeaRegisterPage.path.read[Boolean] and
        businessRelationshipInUkReads and
        TrustResidentInUkPage.path.read[Boolean]
      )(NonMigratingTrustDetails.apply _)
  }

}
