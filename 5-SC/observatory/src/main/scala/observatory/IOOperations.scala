package observatory

import scala.io.Source
import scala.collection.parallel.ParIterable
import collection.parallel.CollectionConverters.ArrayIsParallelizable
import scala.util.Try

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import com.sksamuel.scrimage.implicits.RichImmutableImage
import com.sksamuel.scrimage.implicits.writer
import com.sksamuel.scrimage.ImmutableImage

import java.nio.file.Path
import java.nio.file.Files

object IOOperations {

  def writeTo(path: Path, image: ImmutableImage): Option[Path] =
    print("Called")
    if (!Files.exists(path.getParent))
      Files.createDirectories(path.getParent)
    Try(RichImmutableImage(image).output(path)).toOption

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
