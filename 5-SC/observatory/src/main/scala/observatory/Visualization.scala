package observatory

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import com.sksamuel.scrimage.metadata.ImageMetadata
import com.sksamuel.scrimage.implicits.given
import scala.collection.parallel.CollectionConverters.given
import scala.math.{Pi, acos, cos, pow, sin, abs}

/** 2nd milestone: basic visualization
  */

object VisualizationConstants {
  val earthRadius = 6371.0
  val minDistance = 1.0
}

object Visualization extends VisualizationInterface:

  /** @param temperatures
    *   Known temperatures: pairs containing a location and the temperature at
    *   this location
    * @param location
    *   Location where to predict the temperature
    * @return
    *   The predicted temperature at `location`
    */
  def predictTemperature(
      temperatures: Iterable[(Location, Temperature)],
      location: Location
  ): Temperature =
    def toRad(degree: Double): Double = degree * Pi / 180

    def dist(from: Location, to: Location): Double =
      if (from == to) then 0
      else if from.lat * -1 == to.lat && (180 - from.lon) * -1 == to.lon then Pi
      else
        val delta = acos(
          sin(toRad(from.lat)) * sin(toRad(to.lat)) + cos(
            toRad(from.lat)
          ) * cos(
            toRad(to.lat)
          ) * cos(toRad(abs(from.lon - to.lon)))
        )
        VisualizationConstants.earthRadius * delta

    def idw(
        data: List[(Location, Temperature)],
        numAcc: Double,
        deAcc: Double
    ): Temperature = ???

    val minLoc =
      temperatures.toArray.par.minBy((tuple) => dist(location, tuple._1))

    if (dist(minLoc._1, location) < VisualizationConstants.minDistance) then
      minLoc._2
    else idw(temperatures.toList, 0, 0)

  /** @param points
    *   Pairs containing a value and its associated color
    * @param value
    *   The value to interpolate
    * @return
    *   The color that corresponds to `value`, according to the color scale
    *   defined by `points`
    */
  def interpolateColor(
      points: Iterable[(Temperature, Color)],
      value: Temperature
  ): Color =
    ???

  /** @param temperatures
    *   Known temperatures
    * @param colors
    *   Color scale
    * @return
    *   A 360×180 image where each pixel shows the predicted temperature at its
    *   location
    */
  def visualize(
      temperatures: Iterable[(Location, Temperature)],
      colors: Iterable[(Temperature, Color)]
  ): ImmutableImage =
    ???
