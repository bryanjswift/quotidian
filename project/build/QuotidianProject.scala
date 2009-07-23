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

	val srcMainLib = "src" / "main" / "lib" ** "*.jar"

	override def unmanagedClasspath = super.unmanagedClasspath +++ gaeUserJars +++ gaeSharedJars +++ srcMainLib
}
