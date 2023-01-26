import play.core.PlayVersion
import sbt._

object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"             % "0.74.0",
    "uk.gov.hmrc"       %% "play-frontend-hmrc"             % "6.2.0-play-28",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.12.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"     % "5.25.0",
    "com.typesafe.play" %% "play-json-joda"                 % "2.9.4",
    "uk.gov.hmrc"       %% "domain"                         % "8.1.0-play-28"
  )

  val test: Seq[ModuleID] = Seq(
    "com.typesafe.play"       %% "play-test"                % PlayVersion.current,
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "5.1.0",
    "org.scalatestplus"       %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
    "org.scalatest"           %% "scalatest"                % "3.2.15",
    "org.jsoup"               %  "jsoup"                    % "1.15.3",
    "org.mockito"             %% "mockito-scala-scalatest"  % "1.17.12",
    "com.github.tomakehurst"  %  "wiremock-standalone"      % "2.27.2",
    "wolfendale"              %% "scalacheck-gen-regexp"    % "0.1.2",
    "com.vladsch.flexmark"    %  "flexmark-all"             % "0.62.2",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28"  % "0.74.0"
  ).map(_ % "it, test")

}
