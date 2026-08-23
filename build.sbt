ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := "2.12.15"
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

// Dependencies versions
lazy val sparkVersion = "3.5.0"
lazy val sttpVersion = "4.0.0"
lazy val json4sVersion = "3.7.0-M11"
lazy val scalaTestVersion = "3.2.20"

// Compile dependencies
lazy val sparkCore = "org.apache.spark" %% "spark-core" % sparkVersion
lazy val sparkSql = "org.apache.spark" %% "spark-sql" % sparkVersion
lazy val sttpCore = "com.softwaremill.sttp.client4" %% "core" % sttpVersion
lazy val json4sNative = "org.json4s" %% "json4s-native" % json4sVersion

// Test dependencies
lazy val scalactic = "org.scalactic" %% "scalactic" % scalaTestVersion
lazy val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion % Test

lazy val connector = (project in file("connector"))
  .settings(
    libraryDependencies ++= Seq(
      sparkCore % Provided,
      sparkSql % Provided,
      sttpCore,
      json4sNative,
      scalactic,
      scalaTest
    )
)

lazy val integration = (project in file("integration")).settings(
  libraryDependencies ++= Seq(
    scalactic,
    scalaTest
  )
).dependsOn(
  connector % "test->test"
)

lazy val root = (project in file("."))
  .aggregate(connector, integration)
  .settings(
    name := "arcgis-spark-connector"
  )
