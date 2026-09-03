import BuildSupport.{constants, functions}
import xerial.sbt.Sonatype.sonatypeCentralHost

ThisBuild / organization := constants.ORGANIZATION
ThisBuild / description := "ARCGIS Online and Enterprise connector for Apache Spark"
ThisBuild / homepage := Some(url(constants.PROJECT_URL))
ThisBuild / licenses += (
  "Apache License 2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt")
  )
ThisBuild / developers ++= constants.DEVELOPERS
ThisBuild / scmInfo := Some(
  ScmInfo(
    url(constants.PROJECT_URL),
    "scm:git:git://github.com/dejarol/arcgis-spark-connector.git"
  )
)
ThisBuild / versionScheme := Some("early-semver")

ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := "2.12.15"
ThisBuild / compileOrder := CompileOrder.JavaThenScala
ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-release", "11",
  "-Ywarn-unused:implicits",
  "-Ywarn-unused:imports",
  "-Ywarn-unused:locals",
  "-Ywarn-unused:params",
  "-Ywarn-unused:privates"
)
ThisBuild / javacOptions ++= Seq(
  "-source", "11",
  "-target", "11"
)

ThisBuild / autoAPIMappings := true
ThisBuild / test / parallelExecution := false
ThisBuild / test / logBuffered := false
ThisBuild / Test / testOptions += Tests.Argument("-oD")

// Compile dependencies
lazy val sparkCore = "org.apache.spark" %% "spark-core" % constants.SPARK_VERSION
lazy val sparkSql = "org.apache.spark" %% "spark-sql" % constants.SPARK_VERSION
lazy val sttpCore = "com.softwaremill.sttp.client4" %% "core" % constants.STTP_VERSION
lazy val json4sNative = "org.json4s" %% "json4s-native" % constants.JSON4S_VERSION

// Test dependencies
lazy val scalactic = "org.scalactic" %% "scalactic" % constants.SCALA_TEST_VERSION
lazy val scalaTest = "org.scalatest" %% "scalatest" % constants.SCALA_TEST_VERSION % Test

// Connector
lazy val connector = (project in file("connector"))
  .settings(
    name := constants.ARTIFACT_NAME,
    libraryDependencies ++= Seq(
      sparkCore % Provided,
      sparkSql % Provided,
      sttpCore,
      json4sNative,
      scalactic,
      scalaTest
    ),


    // Enrich API mappings for scaladoc generation
    apiMappings ++= {

      val filesAndModules: Seq[(Attributed[File], ModuleID)] = (Compile / fullClasspath)
        .value.flatMap {
          entry => entry.get(moduleID.key).map(entry -> _)
        }

      functions.collectSparkAPIMappings(filesAndModules)
    },

    // Publishing options
    publishMavenStyle := true,
    publishTo := sonatypePublishToBundle.value,
    sonatypeCredentialHost := sonatypeCentralHost
  )

// Integration
lazy val integration = (project in file("integration"))
  .settings(
    name := f"${constants.PROJECT_NAME}-integration",
    publish / skip := true,
    libraryDependencies ++= Seq(
      sparkCore % Provided,
      sparkSql % Provided,
      scalactic,
      scalaTest
    )
  ).dependsOn(
    connector % "test->test"
  )

// Root
lazy val root = (project in file("."))
  .aggregate(connector, integration)
  .settings(
    name := constants.PROJECT_NAME
  )
