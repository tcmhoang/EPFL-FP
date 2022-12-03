package observatory

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import com.sksamuel.scrimage.metadata.ImageMetadata

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.math.{pow, floor, ceil}

/** 5th milestone: value-added information visualization
  */
object Visualization2 extends Visualization2Interface:

  /** @param point
    *   (x, y) coordinates of a point in the grid cell
    * @param d00
    *   Top-left value
    * @param d01
    *   Bottom-left value
    * @param d10
    *   Top-right value
    * @param d11
    *   Bottom-right value
    * @return
    *   A guess of the value at (x, y) based on the four known values, using
    *   bilinear interpolation See
    *   https://en.wikipedia.org/wiki/Bilinear_interpolation#Unit_Square
    */
  def bilinearInterpolation(
      point: CellPoint,
      d00: Temperature,
      d01: Temperature,
      d10: Temperature,
      d11: Temperature
  ): Temperature =
    val (x, y) = (point.x, point.y)
    d00 * (1 - x) * (1 - y) + d01 * (1 - x) * y + d10 * x * (1 - y) + d11 * x * y

  /** @param grid
    *   Grid to visualize
    * @param colors
    *   Color scale to use
    * @param tile
    *   Tile coordinates to visualize
    * @return
    *   The image of the tile at (x, y, zoom) showing the grid using the given
    *   color scale
    */
  def visualizeGrid(
      grid: GridLocation => Temperature,
      colors: Iterable[(Temperature, Color)],
      tile: Tile
  ): ImmutableImage =
    val buffer = new Array[Pixel](256 * 256)
    val (x, y, zlv) = (tile.x, tile.y, tile.zoom)
    Await.result(
      Future {
        for
          y1 <- 0 until 256
          x1 <- 0 until 256
        yield
          val loc = Interaction.tileLocation(
            Tile(x * pow(2, 8).toInt + x1, y * pow(2, 8).toInt + y1, zlv + 8)
          )

          val (xbase, ybase) = (loc.lat.toInt, loc.lon.toInt)

          val mapCoordToTemp = (for
            y2 <- 0 to 1
            x2 <- 0 to 1
          yield (x2, y2) -> grid(GridLocation(xbase + x2, ybase + y2))).toMap

          val temp = bilinearInterpolation(
            CellPoint(loc.lat - loc.lat.toInt, loc.lon - loc.lon.toInt),
            mapCoordToTemp((0, 0)),
            mapCoordToTemp((0, 1)),
            mapCoordToTemp((1, 0)),
            mapCoordToTemp((1, 1))
          )

          val color = Visualization.interpolateColor(colors, temp)

          buffer(y1 * 256 + x1) =
            Pixel(x1, y1, color.red, color.green, color.blue, 127)
      },
      15.minutes
    )
    ImmutableImage.wrapPixels(256, 256, buffer, ImageMetadata.empty)
