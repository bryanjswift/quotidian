package quotidian.web

import com.handinteractive.mobile.UAgentInfo
import javax.servlet.http.{HttpServlet, HttpServletRequest => Request, HttpServletResponse => Response}
import quotidian.Logging
import scala.util.matching.Regex
import velocity.VelocityView

trait QcServlet extends HttpServlet {
  lazy val html = "templates/default.vm"
  lazy val json = "templates/default.json.vm"
  lazy val xml = "templates/default.xml.vm"
	lazy val plist = "templates/default.plist.vm"

  override def doGet(request:Request, response:Response) = handleRequest(request,response,processGet(_))
  override def doPost(request:Request, response:Response) = handleRequest(request,response,processPost(_))

  def processGet(http:HttpHelper) = Map[String,Any]()
  def processPost(http:HttpHelper) = Map[String,Any]()
  def handleRequest(request:Request,response:Response,processor:(HttpHelper) => Map[String,Any]) = {
    val http = new HttpHelper(request,response)
    val context = processor(http) + ("UserAgentInfo" -> new UAgentInfo(request))
		val view = new VelocityView(http.view)
    view.render(context,request,response)
  }

	private val uriRE = new Regex("(.*?)(xml|html|json|plist)?$","uri","format")
	private val dataRE = new Regex("(.*?)/([^/]*)$","path","data")

  class HttpHelper(val request:Request,val response:Response) extends Logging {
		// pretty impossible to not match this RE
		private val uriMatch = uriRE.findFirstMatchIn(request.getRequestURI).get
		private val uri = trim(uriMatch.group("uri"))
		private val format = uriMatch.group("format") match {
			case null => "html"
			case s:String => s
		}
		private val servletPath = request.getServletPath
		val (path,data) =
			if (servletPath == uri) {
				(uri,"")
			} else {
				(servletPath,uri.replace(servletPath + "/",""))
			}
		lazy val view = format match {
			case "xml" => xml
			case "json" => json
			case "plist" => plist
			case _ => html
		}
  
    def apply(param:String,default:String = "") = {
      val value = request.getParameter(param)
      if (value == null || value == "" || value == default) None else Some(value)
    }
		private def trim(str:String) =
			if (str.length > 0 && str.last == '/') {
				str.substring(0,str.length - 1)
			} else {
				str
			}
  }
}
