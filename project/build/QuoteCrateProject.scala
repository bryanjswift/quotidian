import java.io.File
import sbt._
import Process._

class QuoteCrateProject(info:ProjectInfo) extends DefaultWebProject(info) {
	// locate the Home directory
	val userHome = system[File]("user.home")
	// define custom property
	val defaultGaeHome = userHome.value + "/Documents/src/gae/" + "appengine-java-sdk-1.2.5"
	val gaeHome = propertyOptional[String](defaultGaeHome)
	val lessCompiler = propertyOptional[String]("lessc")

	// repository locations
	val javaNet = "Java.net Repository for Maven" at "http://download.java.net/maven/2/"

	// dependencies for compiling
	val velocity = "org.apache.velocity" % "velocity" % "1.6.1"
	val commonsCollections = "commons-collections" % "commons-collections" % "3.2.1"
	val commonsLang = "commons-lang" % "commons-lang" % "2.4"
	// Lucene
	val luceneCore = "org.apache.lucene" % "lucene-core" % "2.4.1"
	// Jersey
	val jersey = "com.sun.jersey" % "jersey-server" % "1.1.2-ea"

	// Dependencies for testing
	val junit = "junit" % "junit" % "4.7" % "test->default"
	val specs = "org.scala-tools.testing" % "specs" % "1.6.1" % "test->default"

	// App Engine paths
	val gaeSharedJars = Path.fromFile(gaeHome.value) / "lib" / "shared" * "*.jar"
	val gaeTestingJars = Path.fromFile(gaeHome.value) / "lib" / "impl" * "*.jar"

	val jars = gaeSharedJars
	val testingJars = gaeTestingJars

	// if lessc is on $PATH, call it on any outdated .less files in webappPath
	def lessc(sources:PathFinder):Task = {
		val products = for (path <- sources.get) yield Path.fromString(".",path.toString.replaceAll("less$","css"))
		fileTask("less", products from sources) {
			try {
				val paths = lessFiles.getPaths
				for (path <- paths) { (lessCompiler.value + " " + path) ! log }
				None
			} catch {
				case e:Exception => Some(e.getMessage)
			}
		}
	}
	val lessFiles = webappPath ** "*.less"
	lazy val lessCompile = lessc(lessFiles)

	// override looking for jars in ./lib
	override def dependencyPath = "src" / "main" / "lib"
	// override output of war to target/webapp
	override def temporaryWarPath = outputPath / "war"
	// compile with App Engine jars
	override def compileClasspath = super.compileClasspath +++ jars
	// webapp classpath with App Engine jars
	override def webappClasspath = super.webappClasspath
	// add App Engine jars to console classpath
	override def consoleClasspath = super.consoleClasspath +++ jars +++ testingJars
	// compile tests with App Engine jars
	override def testClasspath = super.testClasspath +++ jars +++ testingJars
	// override path to managed dependency cache
	override def managedDependencyPath = "project" / "lib_managed"
	// override webapp resources to filter out .less files
	override def webappResources = descendents(webappPath ##, "*") +++ extraWebappFiles --- lessFiles
	// modify the prepareWebappAction to compile .less files
	override def prepareWebappAction = super.prepareWebappAction dependsOn(lessCompile)
}
