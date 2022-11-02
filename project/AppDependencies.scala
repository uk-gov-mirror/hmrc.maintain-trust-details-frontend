import play.core.PlayVersion
import sbt._

object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28" % "0.73.0",
    "uk.gov.hmrc"       %% "play-frontend-hmrc"             % "1.8.0-play-28",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.11.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"     % "5.24.0",
    "com.typesafe.play" %% "play-json-joda"                 % "2.9.2",
    "uk.gov.hmrc"       %% "domain"                         % "8.1.0-play-28"
  )

  val test: Seq[ModuleID] = Seq(
    "com.typesafe.play"       %% "play-test"                % PlayVersion.current,
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "5.1.0",
    "org.scalatestplus"       %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
    "org.scalatest"           %% "scalatest"                % "3.2.14",
    "org.jsoup"               %  "jsoup"                    % "1.15.3",
    "org.mockito"             %% "mockito-scala-scalatest"  % "1.17.12",
    "com.github.tomakehurst"  %  "wiremock-standalone"      % "2.27.2",
    "wolfendale"              %% "scalacheck-gen-regexp"    % "0.1.2",
    "com.vladsch.flexmark"    %  "flexmark-all"             % "0.62.2",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28"  % "0.73.0"
  ).map(_ % "it, test")

  private val akkaVersion = "2.6.12"
  private val akkaHttpVersion = "10.2.3"

  val overrides = Seq(
    "com.typesafe.akka" %% "akka-stream_2.12"     % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf_2.12"   % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j_2.12"      % akkaVersion,
    "com.typesafe.akka" %% "akka-actor_2.12"      % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core_2.12"  % akkaHttpVersion,
    "commons-codec"     % "commons-codec"         % "1.12"
  )

}
