package observatory

import scala.io.Source
import scala.collection.parallel.ParIterable
import collection.parallel.CollectionConverters.ArrayIsParallelizable
import scala.util.Try

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import com.sksamuel.scrimage.implicits

object IOOperations {

  // TODO: Implement wriing image to path

  def readData(path: String): Option[ParIterable[String]] =
    Await.result(
      Future {
        Try(
          Source
            .fromInputStream(getClass.getResourceAsStream(path), "utf-8")
        ).toOption.map(_.getLines().toArray.par),
      },
      15.minutes
    )

}
