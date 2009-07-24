import java.io.File
import sbt._

class QuotidianProject(info:ProjectInfo) extends DefaultWebProject(info) {
	val userHome = system[File]("user.home")

	val velocity = "org.apache.velocity" % "velocity" % "1.6.1"
	val commonsCollections = "commons-collections" % "commons-collections" % "3.2.1"
	val commonsLang = "commons-lang" % "commons-lang" % "2.4"

	val gaeHome = Path.fromFile(userHome.value + "/Documents/src/gae/") / "appengine-java-sdk-1.2.2"
	val gaeUserJars = gaeHome / "lib" / "user" ** "*.jar"
	val gaeSharedJars = gaeHome / "lib" / "shared" ** "*.jar"

	val libs = "src" / "main" / "lib" ** "*.jar"

	override def compileClasspath = super.compileClasspath +++ gaeUserJars +++ libs +++ gaeSharedJars 

	//override def testDependencies = Set("org.scala-tools.testing" % "specs" % "1.5.0") ++ super.testDependencies
}
