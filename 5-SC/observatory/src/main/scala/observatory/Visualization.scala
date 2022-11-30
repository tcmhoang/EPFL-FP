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
  val idwPower = 3 // shoud be < 2
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
      else if from.lat * -1 == to.lat && (180 - from.lon) * -1 == to.lon then
        Pi * VisualizationConstants.earthRadius
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
    ): Temperature = data match
      case (loc, temp) :: datap =>
        val w = 1 / pow(dist(loc, location), VisualizationConstants.idwPower)
        idw(datap, numAcc + w * temp, deAcc + w)
      case Nil => numAcc / deAcc

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
    val sortedPoints = points.toList.sortBy(_._1)
    val pointRanges = sortedPoints.zip(sortedPoints.tail)
    def getColor(
        pRange: List[((Temperature, Color), (Temperature, Color))]
    ): Color =
      pRange match
        case ((lt, lc), (ht, hc)) :: prp =>
          if value <= lt then lc
          else if value == ht then hc
          else if lt < value && value < ht then
            val delta = ht - lt
            val lwv = (ht - value) / delta
            val hwv = (value - lt) / delta
            (lc, hc) match
              case (Color(lr, lg, lb), Color(hr, hg, hb)) =>
                Color(
                  math.rint(lr * lwv + hr * hwv).toInt,
                  math.rint(lg * lwv + hg * hwv).toInt,
                  math.rint(lb * lwv + hb * hwv).toInt
                )
          else
            prp match
              case Nil => hc
              case _   => getColor(prp)
        case _ => throw new Error("Cannot happend")
    getColor(pointRanges)

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
    def coordToLocation(x: Int, y: Int) = Location(90 - y, x - 180)
    def toPixel(c: Color, x: Int, y: Int) =
      Pixel(x, y, c.red, c.green, c.blue, 255)

    val buffer = new Array[Pixel](360 * 180)
    for
      y <- 0 until 180
      x <- 0 until 360
    do
      buffer(y * 360 + x) = toPixel(
        interpolateColor(
          colors,
          predictTemperature(temperatures, coordToLocation(x, y))
        ),
        x,
        y
      )
    ImmutableImage.wrapPixels(360, 180, buffer, ImageMetadata.empty)
