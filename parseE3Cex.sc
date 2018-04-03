
import edu.holycross.shot.cite._
import scala.io.Source



val hmtIctBase = "http://www.homermultitext.org/ict2/"
val hmtIipSrvBase = "http://www.homermultitext.org/iipsrv?OBJ=IIP,1.0&FIF=/project/homer/pyramidal/deepzoom/"

def iipSrvUrl(img: Cite2Urn,  width: Int = 150, baseUrl: String = hmtIipSrvBase): String = {
  val trail = s"&WID=${width}&CVT=JPEG"
  val imageOnly = List(baseUrl, img.namespace, img.collection, img.version, img.dropExtensions.objectOption.get).mkString("/") + s".tif"

  img.objectExtensionOption match {
    case None => imageOnly +  trail
    case roi:  Some[String] =>imageOnly + "&RGN=" + roi.get + trail
  }
}




val f = "e3-quires.cex"

val lines = Source.fromFile(f).getLines.toVector

val content = for (l <- lines.tail) yield {
  val cols =  l.split("#")
  val img = Cite2Urn(cols(0))
  val folio = Cite2Urn(cols(1))
  s"[![${folio}](${iipSrvUrl(img)})](${hmtIctBase}?urn=${img}) | ${cols(3)} | Folio ${folio.objectComponent}, **${cols(4)}** marks *${cols(2)}* of quire.  " 
}



val hdr = """
# HMT project: quire analysis of Upsilon 1.1 manuscript

Stephanie Lindeborg and Neil Curran

Summer, 2012


| Image | Quire number | Observations |
|:-----------|:-----------|:----------
"""


import java.io.PrintWriter
new PrintWriter("e3quires.md"){write(hdr + content.mkString("\n") +"\n"); close;}
