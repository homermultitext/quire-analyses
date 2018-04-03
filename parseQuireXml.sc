import scala.xml._

// TEI / text / body  / div / table


val f = "QuireNumbers-E3.xml"

val root = XML.loadFile(f)

val rows = root \\ "row"

val cex = for (r <- rows.toVector) yield {
  val cells = (r \\ "cell").toVector
  s"${cells(0).text}#${cells(1).text}#${cells(2).text}#${cells(3).text}#${cells(4).text}"
}

import java.io.PrintWriter

new PrintWriter("e3-quires.cex") {write (cex.mkString("\n")); close; }
