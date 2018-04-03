
import edu.holycross.shot.cite._
import scala.io.Source

val f = "cex/e3-quires-2.cex"

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

val lines = Source.fromFile(f).getLines.toVector
//Image-top#Image-bottom#Folio-top#Folio-bottom#Number#Notes
val content = for (l <- lines.tail) yield {
  val cols =  l.split("#").toVector
  println("COLUMNS: " + cols)

  val folio1 =
    try {
       val urn = Cite2Urn(cols(2))
      s"**${urn.objectComponent}** (`${urn}`)"
    } catch {
      case _ : Throwable => {
          if (cols.size > 2) {s"**${cols(2)}**"} else {""}
      }
    }

  val folio2 =
    try {
       val urn = Cite2Urn(cols(3))
       s"**${urn.objectComponent}** (`${urn}`)"
    } catch {
      case _ : Throwable => {
          if (cols.size > 3) { s"**${cols(3)}**"} else {""}
      }
    }
  val img1Link = try {
    val img = Cite2Urn(cols(0))
    s"[![](${iipSrvUrl(img)})](${hmtIctBase}?urn=${img})"
  } catch {
    case _ : Throwable => {
        if (cols.size > 0) {s"**${cols(0)}**"} else {""}
    }
  }
  val img2Link = try {
    val img = Cite2Urn(cols(1))
    s"[![](${iipSrvUrl(img)})](${hmtIctBase}?urn=${img})"
  } catch {
    case _ : Throwable => {
        if (cols.size > 1) {s"**${cols(1)}**"} else {""}
    }
  }

  val notes = if(cols.size > 5) {cols(5)} else ""
  println(img1Link + " & " + img2Link + " from " + cols)

  println("Num cols == " + cols.size + s"(${cols})")
  val quireNum = cols(4)
  s" **${quireNum}** | ${img1Link} | ${folio1} | ${img2Link} |  ${folio2} | ${notes} "
  //s"[![${folio}](${iipSrvUrl(img)})](${hmtIctBase}?urn=${img}) | ${cols(3)} | Folio ${folio.objectComponent}, **${cols(4)}** marks *${cols(2)}* of quire.  "
}



val hdr = """
# HMT project: quire analysis of Upsilon 1.1 manuscript

Stephanie Lindeborg and Neil Curran

Summer, 2012


Thumbnail images are linked to zoomable views.

|Quire number | Image: top of quire| Folio: top | Image: bottom of quire   |  Folio: bottom | Observations |
|:-----------|:-----------|:----------|:----------|:----------|:----------
"""
println( content.mkString("\n"))

import java.io.PrintWriter
new PrintWriter("views/e3quires-2.md"){write(hdr + content.mkString("\n") +"\n"); close;}
