ThisBuild / name := "lotos"
ThisBuild / scalaVersion := "2.13.1"

lazy val scalaVersions = List("2.13.1")

lazy val commonDependencies =
  libraryDependencies ++= List(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "org.typelevel"  %% "cats-core"    % "2.0.0",
    "org.typelevel"  %% "cats-effect"  % "2.1.1",
    "com.chuusai"    %% "shapeless"    % "2.3.3",
    "org.scalatest"  %% "scalatest"    % "3.1.1" % "test",
  )

def configure(id: String)(project: Project): Project =
  project.settings(
    moduleName := s"lotos-$id",
    crossScalaVersions := scalaVersions,
    sources in (Compile, doc) := List.empty,
    commonDependencies,
    scalacOptions ++= List(
      "-language:experimental.macros",
    ),
    libraryDependencies += compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 13)) => Nil
        case _             => List(compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.patch))
      }
    },
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 13)) => List("-Ymacro-annotations")
        case _             => Nil
      }
    }
  )

def lotosModule(id: String) =
  Project(id, file(s"$id"))
    .configure(configure(id))

lazy val lotosInternal = lotosModule("internal")
lazy val lotosMacro = lotosModule("macro")
  .dependsOn(lotosInternal)
  .aggregate(lotosInternal)
lazy val lotosTesting = lotosModule("testing")
  .dependsOn(lotosInternal, lotosMacro)
  .aggregate(lotosInternal, lotosMacro)
lazy val lotosExamples = lotosModule("examples")
  .settings(
    skip in publish := true
  )
  .dependsOn(lotosInternal, lotosTesting)
  .aggregate(lotosInternal, lotosTesting)

lazy val modules: List[ProjectReference] = List(lotosInternal, lotosMacro, lotosTesting, lotosExamples)

lazy val lotos = project
  .in(file("."))
  .settings(
    moduleName := "lotos",
    skip in publish := true
  )
  .aggregate(modules: _*)
