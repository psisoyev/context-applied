lazy val `context-applied` = project
  .in(file("."))
  .dependsOn(core, test)
  .aggregate(core, test)
  .settings(
    publish / skip := true,
    projectSettings,
    crossScalaVersions := Nil
  )

lazy val core = project
  .in(file("core"))
  .settings(
    name := "context-applied",
    projectSettings,
    libraryDependencies += scalaOrganization.value % "scala-compiler" % scalaVersion.value,
    scalacOptions ++= Seq(
      "-Xfatal-warnings",
      "-Xlint",
      "-feature",
      "-language:higherKinds",
      "-deprecation",
      "-unchecked"
    )
  )

lazy val test = project
  .in(file("test"))
  .dependsOn(core)
  .settings(
    publish / skip := true,
    projectSettings,
    addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.3" cross CrossVersion.full),
    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % Test,
    scalacOptions ++= {
      val jar = (core / Compile / packageBin).value
      Seq(s"-Xplugin:${jar.getAbsolutePath}", s"-Jdummy=${jar.lastModified}") // ensures recompile
    },
    scalacOptions ++= Seq(
      "-Xfatal-warnings",
      "-language:higherKinds",
      "-language:postfixOps",
      "-language:implicitConversions",
      "-Ywarn-value-discard" // Warn when non-Unit expression results are unused.
    )
  )

lazy val projectSettings = Seq(
  ThisBuild / publishTo := {
    val nexus = "https://s01.oss.sonatype.org/"
    if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  organization := "io.github.psisoyev", // changed to avoid the confusion, this is a fork, I'm not the author
  licenses ++= Seq(("MIT", url("http://opensource.org/licenses/MIT"))),
  homepage := Some(url("https://github.com/augustjune/context-applied")),
  developers := List(
    Developer("augustjune", "Yura Slinkin", "jurij.jurich@gmail.com", url("https://github.com/augustjune"))
  ),
  scalaVersion := "2.13.14",
  ThisBuild / versionScheme := Some("early-semver")
)
