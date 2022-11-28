package observatory

import scala.io.Source
import scala.collection.parallel.ParIterable
import collection.parallel.CollectionConverters.ArrayIsParallelizable
import scala.util.Try

object IOOperations {

  def readData(path: String): Option[ParIterable[String]] =
    Try(
      Source
        .fromInputStream(getClass.getResourceAsStream(path), "utf-8")
        .getLines()
        .toArray
        .par
    ).toOption

}
