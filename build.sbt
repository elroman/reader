name := """crm"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

scalaVersion := "2.11.7"

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  javaJpa,
  javaJdbc,
  cache,
  javaWs,
  "org.hibernate" % "hibernate-entitymanager" % "5.0.1.Final",
  "mysql" % "mysql-connector-java" % "5.1.36",
  "com.google.code.gson" % "gson" % "2.4",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.4.0",
  "com.typesafe.akka" % "akka-slf4j_2.11" % "2.4.0"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
// test pushing