import sbt._
import sbt.Keys._

object Build extends sbt.Build {
  val baseSettings = Project.defaultSettings ++ Seq(
    scalaVersion := "2.10.2",
    scalacOptions ++= Seq("-feature", "-deprecation")
  )

  lazy val common = Project(
    id = "phpSerData",
    base = file("."),
    settings = baseSettings ++ Seq(
      name := "phpSerData",
      organization := "si.bss.tools.phpSerData",
      version := "0.1-SNAPSHOT",
      initialCommands in console := "import si.bss.tools.phpSerData._",
      resolvers ++= Seq(
        "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"
      ),
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % "1.9.2" % "test",
        "org.apache.commons" % "commons-lang3" % "3.1"
      )
    )
  )
}

