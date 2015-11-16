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

libraryDependencies ++= Seq(
  "com.github.finagle" %% "finch-core" % finchVersion,
  "com.github.finagle" %% "finch-circe" % finchVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  //"com.github.finagle" %% "finch-jackson" % finchVersion,
  "joda-time" % "joda-time" % jodaTimeVersion,
  "com.typesafe.slick" %% "slick" % slickVersion,
  "org.slf4j" % "slf4j-nop" % slf4jVersion,
  "org.postgresql" % "postgresql" % postgreDriverVersion,
  "org.scalaz" %% "scalaz-core" % scalazVersion
)
scalaSource in Compile := baseDirectory.value / "app"

// ******************** RESOLVERS *****************

resolvers ++= Seq(
  Resolver.jcenterRepo,
  Resolver.typesafeIvyRepo("releases")//,
  //"scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
)


    