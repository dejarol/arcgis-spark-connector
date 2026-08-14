ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := "2.12.15"

// Dependencies versions
lazy val sparkVersion = "3.5.0"
lazy val sttpVersion = "4.0.0"
lazy val scalaTestVersion = "3.2.20"

// Compile dependencies
lazy val sparkCore = "org.apache.spark" %% "spark-core" % sparkVersion
lazy val sparkSql = "org.apache.spark" %% "spark-sql" % sparkVersion
lazy val sttpCore = "com.softwaremill.sttp.client4" %% "core" % sttpVersion

// Test dependencies
lazy val scalactic = "org.scalactic" %% "scalactic" % scalaTestVersion
lazy val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion % Test

lazy val connector = (project in file("connector"))
  .settings(
    libraryDependencies ++= Seq(
      sparkCore % Provided,
      sparkSql % Provided,
      sttpCore,
      scalactic,
      scalaTest
    )
)

lazy val integration = (project in file("integration"))

lazy val root = rootProject
  .aggregate(connector, integration)
  .settings(
    name := "arcgis-spark-connector"
  )
