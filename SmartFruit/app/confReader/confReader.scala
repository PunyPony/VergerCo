package confreader

import java.io.{File, FileInputStream}
import play.api.libs.json._

class confReader {}

object confReader {

  def getConf(conf: String) : JsValue = {
    val file = new File(conf)
    val stream = new FileInputStream(file)
    val json = try {Json.parse(stream)} finally {stream.close()}
    json
  }
  def getURL(): String = {
    val conf = getConf("conf/Smartconf.json")
    val url = (conf \ "conf" \ "backendURL").as[String]
    url
  }

  def getObjID(): Int = {
    val conf = getConf("conf/Smartconf.json")
    val id = (conf \ "conf" \ "objID").as[Int]
    id
  }
}
