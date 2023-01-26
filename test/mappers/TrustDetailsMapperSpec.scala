/*
 * Copyright 2023 HM Revenue & Customs
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

import base.SpecBase
import models.Constants.GB
import models.DeedOfVariation._
import models.{MigratingTrustDetails, NonMigratingTrustDetails, NonUKType, ResidentialStatusType, UkType}
import models.TrusteesBased._
import models.TypeOfTrust._
import pages.maintain._
import play.api.libs.json.JsSuccess

import java.time.LocalDate

class TrustDetailsMapperSpec extends SpecBase {

  private val mapper = injector.instanceOf[TrustDetailsMapper]

  private val country: String = "FR"
  private val date: LocalDate = LocalDate.parse("2000-01-01")

  "TrustDetailsMapper" must {

    "successfully map data" when {

      "not migrating" when {

        val baseAnswers = emptyUserAnswers.copy(migratingFromNonTaxableToTaxable = false)

        "TrustOwnUKLandOrPropertyPage and TrustEEAYesNoPage populated" when {

          "BusinessRelationshipYesNoPage populated" in {

            val userAnswers = baseAnswers
              .set(OwnsUkLandOrPropertyPage, true).success.value
              .set(RecordedOnEeaRegisterPage, true).success.value
              .set(BusinessRelationshipInUkPage, true).success.value
              .set(TrustResidentInUkPage, false).success.value
              .set(Schedule3aExemptYesNoPage, true).success.value

            val result = mapper(userAnswers)

            result mustBe JsSuccess(NonMigratingTrustDetails(
              trustUKProperty = true,
              trustRecorded = true,
              trustUKRelation = Some(true),
              trustUKResident = false,
              schedule3aExempt = Some(true)
            ))
          }

          "BusinessRelationshipYesNoPage not populated" in {

            val userAnswers = baseAnswers
              .set(OwnsUkLandOrPropertyPage, true).success.value
              .set(RecordedOnEeaRegisterPage, true).success.value
              .set(TrustResidentInUkPage, true).success.value

            val result = mapper(userAnswers)

            result mustBe JsSuccess(NonMigratingTrustDetails(
              trustUKProperty = true,
              trustRecorded = true,
              trustUKRelation = None,
              trustUKResident = true
            ))
          }
        }
      }

      "migrating" when {

        val baseAnswers = emptyUserAnswers.copy(migratingFromNonTaxableToTaxable = true)

        "governed and administered in UK; set up after settlor died; trustees UK based; never resident offshore; Schedule 3a Exempt" in {

          val userAnswers = baseAnswers.copy(registeredWithDeceasedSettlor = true)
            .set(GovernedByUkLawPage, true).success.value
            .set(AdministeredInUkPage, true).success.value
            .set(SetUpAfterSettlorDiedPage, true).success.value
            .set(OwnsUkLandOrPropertyPage, true).success.value
            .set(RecordedOnEeaRegisterPage, true).success.value
            .set(WhereTrusteesBasedPage, AllTrusteesUkBased).success.value
            .set(CreatedUnderScotsLawPage, true).success.value
            .set(PreviouslyResidentOffshorePage, false).success.value
            .set(Schedule3aExemptYesNoPage, true).success.value

          val result = mapper(userAnswers)

          result mustBe JsSuccess(MigratingTrustDetails(
            lawCountry = None,
            administrationCountry = GB,
            residentialStatus = ResidentialStatusType(uk = Some(UkType(scottishLaw = true, preOffShore = None))),
            trustUKProperty = true,
            trustRecorded = true,
            trustUKRelation = None,
            trustUKResident = true,
            typeOfTrust = WillTrustOrIntestacyTrust,
            deedOfVariation = None,
            interVivos = None,
            efrbsStartDate = None,
            settlorsUkBased = None,
            schedule3aExempt = Some(true)
          ))
        }

        "governed and administered in UK; not set up after settlor died;" +
          "deed of variation set up in addition to will trust; trustees UK based; resident offshore; not Schedule 3a Exempt" in {

          val userAnswers = baseAnswers.copy(registeredWithDeceasedSettlor = true)
            .set(GovernedByUkLawPage, true).success.value
            .set(AdministeredInUkPage, true).success.value
            .set(SetUpAfterSettlorDiedPage, false).success.value
            .set(OwnsUkLandOrPropertyPage, true).success.value
            .set(RecordedOnEeaRegisterPage, true).success.value
            .set(WhereTrusteesBasedPage, AllTrusteesUkBased).success.value
            .set(CreatedUnderScotsLawPage, false).success.value
            .set(PreviouslyResidentOffshorePage, true).success.value
            .set(PreviouslyResidentOffshoreCountryPage, country).success.value
            .set(Schedule3aExemptYesNoPage, false).success.value

          val result = mapper(userAnswers)

          result mustBe JsSuccess(MigratingTrustDetails(
            lawCountry = None,
            administrationCountry = GB,
            residentialStatus = ResidentialStatusType(uk = Some(UkType(scottishLaw = false, preOffShore = Some(country)))),
            trustUKProperty = true,
            trustRecorded = true,
            trustUKRelation = None,
            trustUKResident = true,
            typeOfTrust = DeedOfVariationTrustOrFamilyArrangement,
            deedOfVariation = Some(AdditionToWillTrust),
            interVivos = None,
            efrbsStartDate = None,
            settlorsUkBased = None,
            schedule3aExempt = Some(false)
          ))
        }

        "governed and administered in UK; not set up after settlor died;" +
          "deed of variation not set up in addition to will trust; trustees UK based; resident offshore; Schedule 3a Exempt" in {

          val userAnswers = baseAnswers.copy(registeredWithDeceasedSettlor = false)
            .set(GovernedByUkLawPage, true).success.value
            .set(AdministeredInUkPage, true).success.value
            .set(TypeOfTrustPage, DeedOfVariationTrustOrFamilyArrangement).success.value
            .set(WhyDeedOfVariationCreatedPage, ReplacedWillTrust).success.value
            .set(OwnsUkLandOrPropertyPage, true).success.value
            .set(RecordedOnEeaRegisterPage, true).success.value
            .set(WhereTrusteesBasedPage, AllTrusteesUkBased).success.value
            .set(CreatedUnderScotsLawPage, false).success.value
            .set(PreviouslyResidentOffshorePage, true).success.value
            .set(PreviouslyResidentOffshoreCountryPage, country).success.value
            .set(Schedule3aExemptYesNoPage, true).success.value

          val result = mapper(userAnswers)

          result mustBe JsSuccess(MigratingTrustDetails(
            lawCountry = None,
            administrationCountry = GB,
            residentialStatus = ResidentialStatusType(uk = Some(UkType(scottishLaw = false, preOffShore = Some(country)))),
            trustUKProperty = true,
            trustRecorded = true,
            trustUKRelation = None,
            trustUKResident = true,
            typeOfTrust = DeedOfVariationTrustOrFamilyArrangement,
            deedOfVariation = Some(ReplacedWillTrust),
            interVivos = None,
            efrbsStartDate = None,
            settlorsUkBased = None,
            schedule3aExempt = Some(true)
          ))
        }

        "governed and administered in UK; not set up after settlor died; inter-vivos with holdover relief;" +
          "some trustees UK based; settlors UK based; resident offshore; not Schedule 3a Exempt" in {

          val userAnswers = baseAnswers.copy(registeredWithDeceasedSettlor = false)
            .set(GovernedByUkLawPage, true).success.value
            .set(AdministeredInUkPage, true).success.value
            .set(TypeOfTrustPage, InterVivosSettlement).success.value
            .set(HoldoverReliefClaimedPage, true).success.value
            .set(OwnsUkLandOrPropertyPage, true).success.value
            .set(RecordedOnEeaRegisterPage, true).success.value
            .set(WhereTrusteesBasedPage, InternationalAndUkBasedTrustees).success.value
            .set(SettlorsUkBasedPage, true).success.value
            .set(CreatedUnderScotsLawPage, false).success.value
            .set(PreviouslyResidentOffshorePage, false).success.value
            .set(Schedule3aExemptYesNoPage, false).success.value

          val result = mapper(userAnswers)

          result mustBe JsSuccess(MigratingTrustDetails(
            lawCountry = None,
            administrationCountry = GB,
            residentialStatus = ResidentialStatusType(uk = Some(UkType(scottishLaw = false, preOffShore = None))),
            trustUKProperty = true,
            trustRecorded = true,
            trustUKRelation = None,
            trustUKResident = true,
            typeOfTrust = InterVivosSettlement,
            deedOfVariation = None,
            interVivos = Some(true),
            efrbsStartDate = None,
            settlorsUkBased = Some(true),
            schedule3aExempt = Some(false)
          ))
        }

        "governed and administered outside UK; not set up after settlor died; employee-related with EFRBS;" +
          "some trustees UK based; settlors non-UK based; settlor benefits from assets; Schedule 3a Exempt" in {

          val userAnswers = baseAnswers.copy(registeredWithDeceasedSettlor = false)
            .set(GovernedByUkLawPage, false).success.value
            .set(GoverningCountryPage, country).success.value
            .set(AdministeredInUkPage, false).success.value
            .set(AdministrationCountryPage, country).success.value
            .set(TypeOfTrustPage, EmploymentRelated).success.value
            .set(EfrbsYesNoPage, true).success.value
            .set(EfrbsStartDatePage, date).success.value
            .set(OwnsUkLandOrPropertyPage, true).success.value
            .set(RecordedOnEeaRegisterPage, true).success.value
            .set(WhereTrusteesBasedPage, InternationalAndUkBasedTrustees).success.value
            .set(SettlorsUkBasedPage, false).success.value
            .set(BusinessRelationshipInUkPage, false).success.value
            .set(SettlorBenefitsFromAssetsPage, true).success.value
            .set(Schedule3aExemptYesNoPage, true).success.value

          val result = mapper(userAnswers)

          result mustBe JsSuccess(MigratingTrustDetails(
            lawCountry = Some(country),
            administrationCountry = country,
            residentialStatus = ResidentialStatusType(nonUK = Some(NonUKType(sch5atcgga92 = true, s218ihta84 = None, agentS218IHTA84 = None))),
            trustUKProperty = true,
            trustRecorded = true,
            trustUKRelation = Some(false),
            trustUKResident = false,
            typeOfTrust = EmploymentRelated,
            deedOfVariation = None,
            interVivos = None,
            efrbsStartDate = Some(date),
            settlorsUkBased = Some(false),
            schedule3aExempt = Some(true)
          ))
        }

        "governed and administered outside UK; not set up after settlor died; employee-related without EFRBS;" +
          "some trustees UK based; settlors non-UK based; settlor benefits from assets; not Schedule 3a Exempt" in {

          val userAnswers = baseAnswers.copy(registeredWithDeceasedSettlor = false)
            .set(GovernedByUkLawPage, false).success.value
            .set(GoverningCountryPage, country).success.value
            .set(AdministeredInUkPage, false).success.value
            .set(AdministrationCountryPage, country).success.value
            .set(TypeOfTrustPage, EmploymentRelated).success.value
            .set(EfrbsYesNoPage, false).success.value
            .set(OwnsUkLandOrPropertyPage, true).success.value
            .set(RecordedOnEeaRegisterPage, true).success.value
            .set(WhereTrusteesBasedPage, InternationalAndUkBasedTrustees).success.value
            .set(SettlorsUkBasedPage, false).success.value
            .set(BusinessRelationshipInUkPage, true).success.value
            .set(SettlorBenefitsFromAssetsPage, true).success.value
            .set(Schedule3aExemptYesNoPage, false).success.value

          val result = mapper(userAnswers)

          result mustBe JsSuccess(MigratingTrustDetails(
            lawCountry = Some(country),
            administrationCountry = country,
            residentialStatus = ResidentialStatusType(nonUK = Some(NonUKType(sch5atcgga92 = true, s218ihta84 = None, agentS218IHTA84 = None))),
            trustUKProperty = true,
            trustRecorded = true,
            trustUKRelation = Some(true),
            trustUKResident = false,
            typeOfTrust = EmploymentRelated,
            deedOfVariation = None,
            interVivos = None,
            efrbsStartDate = None,
            settlorsUkBased = Some(false),
            schedule3aExempt = Some(false)
          ))
        }

        "governed and administered outside UK; not set up after settlor died; flat management; no trustees UK based;" +
          "not for purpose of section 218; Schedule 3a Exempt" in {

          val userAnswers = baseAnswers.copy(registeredWithDeceasedSettlor = false)
            .set(GovernedByUkLawPage, false).success.value
            .set(GoverningCountryPage, country).success.value
            .set(AdministeredInUkPage, false).success.value
            .set(AdministrationCountryPage, country).success.value
            .set(TypeOfTrustPage, FlatManagementCompanyOrSinkingFund).success.value
            .set(OwnsUkLandOrPropertyPage, true).success.value
            .set(RecordedOnEeaRegisterPage, true).success.value
            .set(WhereTrusteesBasedPage, NoTrusteesUkBased).success.value
            .set(BusinessRelationshipInUkPage, true).success.value
            .set(SettlorBenefitsFromAssetsPage, false).success.value
            .set(ForPurposeOfSection218Page, false).success.value
            .set(Schedule3aExemptYesNoPage, true).success.value

          val result = mapper(userAnswers)

          result mustBe JsSuccess(MigratingTrustDetails(
            lawCountry = Some(country),
            administrationCountry = country,
            residentialStatus = ResidentialStatusType(nonUK = Some(NonUKType(sch5atcgga92 = false, s218ihta84 = Some(false), agentS218IHTA84 = None))),
            trustUKProperty = true,
            trustRecorded = true,
            trustUKRelation = Some(true),
            trustUKResident = false,
            typeOfTrust = FlatManagementCompanyOrSinkingFund,
            deedOfVariation = None,
            interVivos = None,
            efrbsStartDate = None,
            settlorsUkBased = None,
            schedule3aExempt = Some(true)
          ))
        }

        "governed and administered outside UK; not set up after settlor died; historic buildings;" +
          "no trustees UK based; for purpose of section 218; not Schedule 3a Exempt" in {

          val userAnswers = baseAnswers.copy(registeredWithDeceasedSettlor = false)
            .set(GovernedByUkLawPage, false).success.value
            .set(GoverningCountryPage, country).success.value
            .set(AdministeredInUkPage, false).success.value
            .set(AdministrationCountryPage, country).success.value
            .set(TypeOfTrustPage, HeritageMaintenanceFund).success.value
            .set(OwnsUkLandOrPropertyPage, true).success.value
            .set(RecordedOnEeaRegisterPage, true).success.value
            .set(WhereTrusteesBasedPage, NoTrusteesUkBased).success.value
            .set(BusinessRelationshipInUkPage, true).success.value
            .set(SettlorBenefitsFromAssetsPage, false).success.value
            .set(ForPurposeOfSection218Page, true).success.value
            .set(AgentCreatedTrustPage, true).success.value
            .set(Schedule3aExemptYesNoPage, false).success.value

          val result = mapper(userAnswers)

          result mustBe JsSuccess(MigratingTrustDetails(
            lawCountry = Some(country),
            administrationCountry = country,
            residentialStatus = ResidentialStatusType(nonUK = Some(NonUKType(sch5atcgga92 = false, s218ihta84 = Some(true), agentS218IHTA84 = Some(true)))),
            trustUKProperty = true,
            trustRecorded = true,
            trustUKRelation = Some(true),
            trustUKResident = false,
            typeOfTrust = HeritageMaintenanceFund,
            deedOfVariation = None,
            interVivos = None,
            efrbsStartDate = None,
            settlorsUkBased = None,
            schedule3aExempt = Some(false)
          ))
        }
      }
    }

    "fail to map data" when {

      "not migrating" when {

        val baseAnswers = emptyUserAnswers.copy(migratingFromNonTaxableToTaxable = false)

        "TrustOwnUKLandOrPropertyPage, TrustEEAYesNoPage or TrustUKResidentPage not populated" when {

          "TrustOwnUKLandOrPropertyPage not populated" in {

            val userAnswers = baseAnswers
              .set(RecordedOnEeaRegisterPage, true).success.value
              .set(TrustResidentInUkPage, false).success.value

            val result = mapper.areAnswersSubmittable(userAnswers)

            result mustBe false
          }

          "TrustEEAYesNoPage not populated" in {

            val userAnswers = baseAnswers
              .set(OwnsUkLandOrPropertyPage, true).success.value
              .set(TrustResidentInUkPage, false).success.value

            val result = mapper.areAnswersSubmittable(userAnswers)

            result mustBe false
          }

          "TrustUKResidentPage not populated" in {

            val userAnswers = baseAnswers
              .set(OwnsUkLandOrPropertyPage, true).success.value
              .set(RecordedOnEeaRegisterPage, true).success.value

            val result = mapper.areAnswersSubmittable(userAnswers)

            result mustBe false
          }
        }

        "non-UK resident trust and BusinessRelationshipInUkPage undefined" in {

          val userAnswers = baseAnswers
            .set(OwnsUkLandOrPropertyPage, true).success.value
            .set(RecordedOnEeaRegisterPage, true).success.value
            .set(TrustResidentInUkPage, false).success.value

          val result = mapper.areAnswersSubmittable(userAnswers)

          result mustBe false
        }
      }

      "migrating" when {

        val baseAnswers = emptyUserAnswers.copy(migratingFromNonTaxableToTaxable = true)

        "GovernedByUkLawPage false and GoverningCountryPage undefined" in {

          val userAnswers = baseAnswers.copy(registeredWithDeceasedSettlor = false)
            .set(GovernedByUkLawPage, false).success.value
            .set(AdministeredInUkPage, true).success.value
            .set(TypeOfTrustPage, DeedOfVariationTrustOrFamilyArrangement).success.value
            .set(WhyDeedOfVariationCreatedPage, ReplacedWillTrust).success.value
            .set(OwnsUkLandOrPropertyPage, true).success.value
            .set(RecordedOnEeaRegisterPage, true).success.value
            .set(WhereTrusteesBasedPage, AllTrusteesUkBased).success.value
            .set(CreatedUnderScotsLawPage, true).success.value
            .set(PreviouslyResidentOffshorePage, false).success.value
            .set(Schedule3aExemptYesNoPage, true).success.value

          val result = mapper.areAnswersSubmittable(userAnswers)

          result mustBe false
        }

        "AdministeredInUkPage false and AdministrationCountryPage undefined" in {

          val userAnswers = baseAnswers.copy(registeredWithDeceasedSettlor = false)
            .set(GovernedByUkLawPage, true).success.value
            .set(AdministeredInUkPage, false).success.value
            .set(TypeOfTrustPage, DeedOfVariationTrustOrFamilyArrangement).success.value
            .set(WhyDeedOfVariationCreatedPage, ReplacedWillTrust).success.value
            .set(OwnsUkLandOrPropertyPage, true).success.value
            .set(RecordedOnEeaRegisterPage, true).success.value
            .set(WhereTrusteesBasedPage, AllTrusteesUkBased).success.value
            .set(CreatedUnderScotsLawPage, true).success.value
            .set(PreviouslyResidentOffshorePage, false).success.value
            .set(Schedule3aExemptYesNoPage, false).success.value

          val result = mapper.areAnswersSubmittable(userAnswers)

          result mustBe false
        }

        "TypeOfTrustPage InterVivosSettlement and HoldoverReliefClaimedPage undefined" in {

          val userAnswers = baseAnswers.copy(registeredWithDeceasedSettlor = false)
            .set(GovernedByUkLawPage, true).success.value
            .set(AdministeredInUkPage, true).success.value
            .set(TypeOfTrustPage, InterVivosSettlement).success.value
            .set(OwnsUkLandOrPropertyPage, true).success.value
            .set(RecordedOnEeaRegisterPage, true).success.value
            .set(WhereTrusteesBasedPage, AllTrusteesUkBased).success.value
            .set(CreatedUnderScotsLawPage, true).success.value
            .set(PreviouslyResidentOffshorePage, false).success.value
            .set(Schedule3aExemptYesNoPage, true).success.value

          val result = mapper.areAnswersSubmittable(userAnswers)

          result mustBe false
        }

        "TypeOfTrustPage EmploymentRelated and EfrbsYesNoPage undefined" in {

          val userAnswers = baseAnswers.copy(registeredWithDeceasedSettlor = false)
            .set(GovernedByUkLawPage, true).success.value
            .set(AdministeredInUkPage, true).success.value
            .set(TypeOfTrustPage, EmploymentRelated).success.value
            .set(OwnsUkLandOrPropertyPage, true).success.value
            .set(RecordedOnEeaRegisterPage, true).success.value
            .set(WhereTrusteesBasedPage, AllTrusteesUkBased).success.value
            .set(CreatedUnderScotsLawPage, true).success.value
            .set(PreviouslyResidentOffshorePage, false).success.value
            .set(Schedule3aExemptYesNoPage, false).success.value

          val result = mapper.areAnswersSubmittable(userAnswers)

          result mustBe false
        }

        "EfrbsYesNoPage true and EfrbsStartDate undefined" in {

          val userAnswers = baseAnswers.copy(registeredWithDeceasedSettlor = false)
            .set(GovernedByUkLawPage, true).success.value
            .set(AdministeredInUkPage, true).success.value
            .set(TypeOfTrustPage, EmploymentRelated).success.value
            .set(EfrbsYesNoPage, true).success.value
            .set(OwnsUkLandOrPropertyPage, true).success.value
            .set(RecordedOnEeaRegisterPage, true).success.value
            .set(WhereTrusteesBasedPage, AllTrusteesUkBased).success.value
            .set(CreatedUnderScotsLawPage, true).success.value
            .set(PreviouslyResidentOffshorePage, false).success.value
            .set(Schedule3aExemptYesNoPage, true).success.value

          val result = mapper.areAnswersSubmittable(userAnswers)

          result mustBe false
        }

        "WhereTrusteesBasedPage InternationalAndUkBasedTrustees and SettlorsUkBasedPage undefined" in {

          val userAnswers = baseAnswers.copy(registeredWithDeceasedSettlor = true)
            .set(GovernedByUkLawPage, true).success.value
            .set(AdministeredInUkPage, true).success.value
            .set(SetUpAfterSettlorDiedPage, true).success.value
            .set(OwnsUkLandOrPropertyPage, true).success.value
            .set(RecordedOnEeaRegisterPage, true).success.value
            .set(WhereTrusteesBasedPage, InternationalAndUkBasedTrustees).success.value
            .set(CreatedUnderScotsLawPage, true).success.value
            .set(PreviouslyResidentOffshorePage, false).success.value
            .set(Schedule3aExemptYesNoPage, false).success.value

          val result = mapper.areAnswersSubmittable(userAnswers)

          result mustBe false
        }

        "PreviouslyResidentOffshorePage true and PreviouslyResidentOffshoreCountryPage undefined" in {

          val userAnswers = baseAnswers.copy(registeredWithDeceasedSettlor = true)
            .set(GovernedByUkLawPage, true).success.value
            .set(AdministeredInUkPage, true).success.value
            .set(SetUpAfterSettlorDiedPage, true).success.value
            .set(OwnsUkLandOrPropertyPage, true).success.value
            .set(RecordedOnEeaRegisterPage, true).success.value
            .set(WhereTrusteesBasedPage, AllTrusteesUkBased).success.value
            .set(CreatedUnderScotsLawPage, true).success.value
            .set(PreviouslyResidentOffshorePage, true).success.value
            .set(Schedule3aExemptYesNoPage, true).success.value

          val result = mapper.areAnswersSubmittable(userAnswers)

          result mustBe false
        }

        "SettlorBenefitsFromAssetsPage false and ForPurposeOfSection218Page undefined" in {

          val userAnswers = baseAnswers.copy(registeredWithDeceasedSettlor = true)
            .set(GovernedByUkLawPage, true).success.value
            .set(AdministeredInUkPage, true).success.value
            .set(SetUpAfterSettlorDiedPage, true).success.value
            .set(OwnsUkLandOrPropertyPage, true).success.value
            .set(RecordedOnEeaRegisterPage, true).success.value
            .set(WhereTrusteesBasedPage, NoTrusteesUkBased).success.value
            .set(BusinessRelationshipInUkPage, true).success.value
            .set(SettlorBenefitsFromAssetsPage, false).success.value
            .set(Schedule3aExemptYesNoPage, false).success.value

          val result = mapper.areAnswersSubmittable(userAnswers)

          result mustBe false
        }

        "ForPurposeOfSection218Page true and AgentCreatedTrustPage undefined" in {

          val userAnswers = baseAnswers.copy(registeredWithDeceasedSettlor = true)
            .set(GovernedByUkLawPage, true).success.value
            .set(AdministeredInUkPage, true).success.value
            .set(SetUpAfterSettlorDiedPage, true).success.value
            .set(OwnsUkLandOrPropertyPage, true).success.value
            .set(RecordedOnEeaRegisterPage, true).success.value
            .set(WhereTrusteesBasedPage, NoTrusteesUkBased).success.value
            .set(BusinessRelationshipInUkPage, true).success.value
            .set(SettlorBenefitsFromAssetsPage, false).success.value
            .set(ForPurposeOfSection218Page, true).success.value
            .set(Schedule3aExemptYesNoPage, true).success.value

          val result = mapper.areAnswersSubmittable(userAnswers)

          result mustBe false
        }
      }
    }
  }
}
