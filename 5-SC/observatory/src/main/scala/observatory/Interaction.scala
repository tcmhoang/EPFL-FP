package observatory

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import com.sksamuel.scrimage.metadata.ImageMetadata
import scala.collection.parallel.CollectionConverters.given
import scala.math.{Pi, atan, pow, sinh}

import scala.concurrent.{Await, Future, ExecutionContext}
import scala.concurrent.duration._
import java.util.concurrent.Executors

/** 3rd milestone: interactive visualization
  */
object Interaction extends InteractionInterface:
  given ExecutionContext =
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))

  /** @param tile
    *   Tile coordinates
    * @return
    *   The latitude and longitude of the top-left corner of the tile, as per
    *   http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(tile: Tile): Location = tile match
    case Tile(x, y, zlv) =>
      val epsilon = pow(2, zlv)
      val n = pow(2.0, zlv)

      Location(
        atan(sinh(Pi * (1 - 2 * y / epsilon))) * 180 / Pi,
        x / epsilon * 360 - 180
      )

  /** @param temperatures
    *   Known temperatures
    * @param colors
    *   Color scale
    * @param tile
    *   Tile coordinates
    * @return
    *   A 256×256 image showing the contents of the given tile
    */
  def tile(
      temperatures: Iterable[(Location, Temperature)],
      colors: Iterable[(Temperature, Color)],
      tile: Tile
  ): ImmutableImage =
    tile match
      case Tile(x, y, zlv) =>
        Visualization.auxVisualize(
          temperatures,
          colors,
          (256, 256),
          (xp, yp) =>
            tileLocation(
              Tile(x * pow(2, 8).toInt + xp, y * pow(2, 8).toInt + yp, zlv + 8)
            ),
          127
        )

  /** Generates all the tiles for zoom levels 0 to 3 (included), for all the
    * given years.
    * @param yearlyData
    *   Sequence of (year, data), where `data` is some data associated with
    *   `year`. The type of `data` can be anything.
    * @param generateImage
    *   Function that generates an image given a year, a zoom level, the x and y
    *   coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
      yearlyData: Iterable[(Year, Data)],
      generateImage: (Year, Tile, Data) => Unit
  ): Unit =
    Await.result(
      Future.sequence(
        for
          (year, d) <- yearlyData
          zoom <- 0 to 3
          y <- 0 until pow(2, zoom).toInt
          x <- 0 until pow(2, zoom).toInt
        yield Future(generateImage(year, Tile(x, y, zoom), d))
      ),
      15.minute
    )
