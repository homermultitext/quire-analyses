import edu.holycross.shot.cite._
import scala.io.Source

val f = "cex/vb-quires.cex"
val outfile = "views/vbquires.md"


val hmtIctBase = "http://www.homermultitext.org/ict2/"
val hmtIipSrvBase = "http://www.homermultitext.org/iipsrv?OBJ=IIP,1.0&FIF=/project/homer/pyramidal/deepzoom/"

def iipSrvUrl(img: Cite2Urn,  width: Int = 250, baseUrl: String = hmtIipSrvBase): String = {
  val trail = s"&WID=${width}&CVT=JPEG"
  val imageOnly = List(baseUrl, img.namespace, img.collection, img.version, img.dropExtensions.objectOption.get).mkString("/") + s".tif"

  img.objectExtensionOption match {
    case None => imageOnly +  trail
    case roi:  Some[String] =>imageOnly + "&RGN=" + roi.get + trail
  }
}




val lines = Source.fromFile(f).getLines.toVector

val content = for (l <- lines.tail) yield {
  val cols =  l.split("#")
  // Folios#Iliad lines#Image: front#Image: back#Notes
  val quireNum = cols(0)
  val folios = Cite2Urn(cols(1))
  val iliad = CtsUrn(cols(2))
  val img1Link = try {
    val img = Cite2Urn(cols(3))
    s"[![${folios.rangeBegin.trim}](${iipSrvUrl(img)})](${hmtIctBase}?urn=${img})"
  } catch {
    case _ : Throwable => {
        if (cols.size > 3) {s"**${cols(3)}**"} else {""}
    }
  }
  val img2Link = try {
    val img = Cite2Urn(cols(4))
    s"[![${folios.rangeBegin.trim}](${iipSrvUrl(img)})](${hmtIctBase}?urn=${img})"
  } catch {
    case _ : Throwable =>   if (cols.size > 4) {s"**${cols(4)}**"} else {""}
  }

  val notes = if (cols.size  > 5) {
    cols(5)
  } else {
    ""
  }
  s"**${quireNum}** | ${img1Link} |  ${img2Link} | Folios ${folios.objectComponent} (`${folios}`), *Iliad* ${iliad.passageComponent} | ${notes} "
}


val hdr = """
# HMT project: quire analysis of Venetus B manuscript

Stephanie Lindeborg and Neil Curran

Summer, 2012

Thumbnail images are linked to zoomable views.


| Quire number | Image: top of quire | Image: bottom of  quire | Coverage | Observations |
|:-----------|:-----------|:----------|:----------|:----------|
"""


import java.io.PrintWriter
new PrintWriter(outfile){write(hdr + content.mkString("\n") +"\n"); close;}
