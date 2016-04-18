name := "topicmodeling"

version := "1.0"

//scalaVersion := "2.11.8"
scalaVersion := "2.10.4"

resolvers ++= Seq(
  "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.1"  % "provided"
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "1.6.1"  %"provided"

