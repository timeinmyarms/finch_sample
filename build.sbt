name := "finch_test"

version := "1.0"

scalaVersion := "2.11.7"
val finchVersion = "0.9.0"
val jodaTimeVersion = "2.9"
val slickVersion = "3.1.0"
val slf4jVersion = "1.7.12"
val postgreDriverVersion = "9.4-1201-jdbc41"
val scalazVersion = "7.1.5"
val circeVersion = "0.2.0"
val logbackVesion = "1.1.3"

libraryDependencies ++= Seq(
  "com.github.finagle" %% "finch-core" % finchVersion,
  "com.github.finagle" %% "finch-circe" % finchVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "joda-time" % "joda-time" % jodaTimeVersion,
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
  "org.postgresql" % "postgresql" % postgreDriverVersion,
  "org.scalaz" %% "scalaz-core" % scalazVersion,
  "ch.qos.logback" % "logback-classic" % logbackVesion
)

scalaSource in Compile := baseDirectory.value / "app"
unmanagedClasspath in Runtime += baseDirectory.value / "conf"

// ******************** RESOLVERS *****************

resolvers ++= Seq(
  Resolver.jcenterRepo,
  Resolver.typesafeIvyRepo("releases")//,
  //"scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
)


    