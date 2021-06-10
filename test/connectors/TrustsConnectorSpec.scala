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

package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import controllers.Assets.OK
import models.DeedOfVariation.PreviouslyAbsoluteInterestUnderWill
import models.TypeOfTrust.WillTrustOrIntestacyTrust
import models.http.TaxableMigrationFlag
import models._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Inside}
import play.api.libs.json.{JsBoolean, JsString, Json}
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate

class TrustsConnectorSpec extends SpecBase with ScalaFutures
  with Inside with BeforeAndAfterAll with BeforeAndAfterEach with IntegrationPatience {
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  protected val server: WireMockServer = new WireMockServer(wireMockConfig().dynamicPort())

  override def beforeAll(): Unit = {
    server.start()
    super.beforeAll()
  }

  override def beforeEach(): Unit = {
    server.resetAll()
    super.beforeEach()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    server.stop()
  }

  override val identifier = "1000000008"
  val index = 0

  private def setUkPropertyUrl(identifier: String) = s"/trusts/trust-details/$identifier/uk-property"
  private def setTrustRecordedUrl(identifier: String) = s"/trusts/trust-details/$identifier/recorded"
  private def setUkRelationUrl(identifier: String) = s"/trusts/trust-details/$identifier/uk-relation"
  private def setUkResidentUrl(identifier: String) = s"/trusts/trust-details/$identifier/uk-resident"
  private def getTrustMigrationFlagUrl(identifier: String) = s"/trusts/$identifier/taxable-migration/migrating-to-taxable"
  private def setMigratingTrustDetailsUrl(identifier: String) = s"/trusts/trust-details/$identifier/migrating-trust-details"
  private def setNonMigratingTrustDetailsUrl(identifier: String) = s"/trusts/trust-details/$identifier/non-migrating-trust-details"
  private def wasTrustRegisteredWithDeceasedSettlorUrl(identifier: String) = s"/trusts/trust-details/$identifier/has-deceased-settlor"
  private def getTrustNameUrl(identifier: String) = s"/trusts/trust-details/$identifier/trust-name"
  private def removeTrustTypeDependentTransformsUrl(identifier: String) = s"/trusts/$identifier/trust-type-dependent-transforms"
  private def removeOptionalTrustDetailTransformsUrl(identifier: String) = s"/trusts/$identifier/optional-trust-detail-transforms"

  "trust connector" must {

    "getTrustDetails" in {

      val json = Json.parse(
        """
          |{
          | "startDate": "1920-03-28",
          | "lawCountry": "AD",
          | "administrationCountry": "GB",
          | "residentialStatus": {
          |   "uk": {
          |     "scottishLaw": false,
          |     "preOffShore": "AD"
          |   }
          | },
          | "typeOfTrust": "Will Trust or Intestacy Trust",
          | "deedOfVariation": "Previously there was only an absolute interest under the will",
          | "interVivos": false
          |}
          |""".stripMargin)

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustsConnector]

      server.stubFor(
        get(urlEqualTo(s"/trusts/trust-details/$identifier/transformed"))
          .willReturn(okJson(json.toString))
      )

      val processed = connector.getTrustDetails(identifier)

      whenReady(processed) {
        r =>
          r mustBe TrustDetailsType(
            startDate = LocalDate.parse("1920-03-28"),
            lawCountry = Some("AD"),
            administrationCountry = Some("GB"),
            residentialStatus = Some(ResidentialStatusType(Some(UkType(scottishLaw = false, preOffShore = Some("AD"))), None)),
            trustRecorded = None,
            trustUKProperty = None,
            trustUKRelation = None,
            trustUKResident = None,
            typeOfTrust = Some(WillTrustOrIntestacyTrust),
            deedOfVariation = Some(PreviouslyAbsoluteInterestUnderWill),
            interVivos = Some(false),
            efrbsStartDate = None
          )
      }
    }

    "setUkProperty" in {

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustsConnector]

      server.stubFor(
        put(urlEqualTo(setUkPropertyUrl(identifier)))
          .willReturn(ok)
      )

      val result = connector.setUkProperty(identifier, value = true)

      result.futureValue.status mustBe OK

      application.stop()
    }

    "setTrustRecorded" in {

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustsConnector]

      server.stubFor(
        put(urlEqualTo(setTrustRecordedUrl(identifier)))
          .willReturn(ok)
      )

      val result = connector.setTrustRecorded(identifier, value = true)

      result.futureValue.status mustBe OK

      application.stop()
    }

    "setUkRelation" in {

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustsConnector]

      server.stubFor(
        put(urlEqualTo(setUkRelationUrl(identifier)))
          .willReturn(ok)
      )

      val result = connector.setUkRelation(identifier, value = true)

      result.futureValue.status mustBe OK

      application.stop()
    }

    "setUkResident" in {

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustsConnector]

      server.stubFor(
        put(urlEqualTo(setUkResidentUrl(identifier)))
          .willReturn(ok)
      )

      val result = connector.setUkResident(identifier, value = true)

      result.futureValue.status mustBe OK

      application.stop()
    }

    "getTrustMigrationFlag" when {

      "value defined" in {

        val json = Json.parse(
          """
            |{
            | "value": true
            |}
            |""".stripMargin)

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.trusts.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[TrustsConnector]

        server.stubFor(
          get(urlEqualTo(getTrustMigrationFlagUrl(identifier)))
            .willReturn(okJson(json.toString))
        )

        val result = connector.getTrustMigrationFlag(identifier)

        whenReady(result) { r =>
          r mustBe TaxableMigrationFlag(Some(true))
        }

        application.stop()
      }

      "value undefined" in {

        val json = Json.parse(
          """
            |{}
            |""".stripMargin)

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.trusts.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[TrustsConnector]

        server.stubFor(
          get(urlEqualTo(getTrustMigrationFlagUrl(identifier)))
            .willReturn(okJson(json.toString))
        )

        val result = connector.getTrustMigrationFlag(identifier)

        whenReady(result) { r =>
          r mustBe TaxableMigrationFlag(None)
        }

        application.stop()
      }
    }

    "setMigratingTrustDetails" in {

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustsConnector]

      server.stubFor(
        put(urlEqualTo(setMigratingTrustDetailsUrl(identifier)))
          .willReturn(ok)
      )

      val result = connector.setMigratingTrustDetails(
        identifier,
        MigratingTrustDetails(
          lawCountry = None,
          administrationCountry = "GB",
          residentialStatus = ResidentialStatusType(uk = Some(UkType(scottishLaw = true, preOffShore = None))),
          trustUKProperty = true,
          trustRecorded = true,
          trustUKRelation = None,
          trustUKResident = true,
          typeOfTrust = WillTrustOrIntestacyTrust,
          deedOfVariation = None,
          interVivos = None,
          efrbsStartDate = None
        )
      )

      result.futureValue.status mustBe OK

      application.stop()
    }

    "setNonMigratingTrustDetails" in {

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustsConnector]

      server.stubFor(
        put(urlEqualTo(setNonMigratingTrustDetailsUrl(identifier)))
          .willReturn(ok)
      )

      val result = connector.setNonMigratingTrustDetails(
        identifier,
        NonMigratingTrustDetails(
          trustUKProperty = true,
          trustRecorded = true,
          trustUKRelation = None,
          trustUKResident = true
        )
      )

      result.futureValue.status mustBe OK

      application.stop()
    }

    "wasTrustRegisteredWithDeceasedSettlor" in {

      val json = JsBoolean(true)

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustsConnector]

      server.stubFor(
        get(urlEqualTo(wasTrustRegisteredWithDeceasedSettlorUrl(identifier)))
          .willReturn(okJson(json.toString))
      )

      val result = connector.wasTrustRegisteredWithDeceasedSettlor(identifier)

      whenReady(result) { r =>
        r mustBe true
      }

      application.stop()
    }

    "getTrustName" in {

      val trustName = "Trust Name"

      val json = JsString(trustName)

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustsConnector]

      server.stubFor(
        get(urlEqualTo(getTrustNameUrl(identifier)))
          .willReturn(okJson(json.toString))
      )

      val result = connector.getTrustName(identifier)

      whenReady(result) { r =>
        r mustBe trustName
      }

      application.stop()
    }

    "removeTrustTypeDependentTransforms" in {

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustsConnector]

      server.stubFor(
        delete(urlEqualTo(removeTrustTypeDependentTransformsUrl(identifier)))
          .willReturn(ok)
      )

      val result = connector.removeTrustTypeDependentTransforms(identifier)

      result.futureValue.status mustBe OK

      application.stop()
    }

    "removeOptionalTrustDetailTransforms" in {

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustsConnector]

      server.stubFor(
        delete(urlEqualTo(removeOptionalTrustDetailTransformsUrl(identifier)))
          .willReturn(ok)
      )

      val result = connector.removeOptionalTrustDetailTransforms(identifier)

      result.futureValue.status mustBe OK

      application.stop()
    }

  }
}
