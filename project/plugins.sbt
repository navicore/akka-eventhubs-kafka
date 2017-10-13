//addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")

def websudosPattern = {
  val pList = List("[organisation]/[module](_[scalaVersion])(_[sbtVersion])/[revision]/[artifact]-[revision](-[classifier]).[ext]")
  Patterns(pList, pList, true)
}

resolvers ++= Seq(
  Resolver.url("Maven ivy Websudos", url(Resolver.DefaultMavenRepositoryRoot))(websudosPattern)
)

addSbtPlugin("org.ensime" % "sbt-ensime" % "1.12.14")

