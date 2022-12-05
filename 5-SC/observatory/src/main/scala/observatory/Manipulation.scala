package observatory

import scala.collection.parallel.CollectionConverters.given

/** 4th milestone: value-added information
  */
object Manipulation extends ManipulationInterface:

  /** @param temperatures
    *   Known temperatures
    * @return
    *   A function that, given a latitude in [-89, 90] and a longitude in [-180,
    *   179], returns the predicted temperature at this location
    */
  def makeGrid(
      temperatures: Iterable[(Location, Temperature)]
  ): GridLocation => Temperature =
    println("Mk regular grid")
    val cache = collection.mutable.Map[GridLocation, Temperature]()
    (loc: GridLocation) =>
      cache.getOrElseUpdate(
        loc,
        Visualization.predictTemperature(
          temperatures,
          Location(loc.lat, loc.lon)
        )
      )

  /** @param temperaturess
    *   Sequence of known temperatures over the years (each element of the
    *   collection is a collection of pairs of location and temperature)
    * @return
    *   A function that, given a latitude and a longitude, returns the average
    *   temperature at this location
    */
  def average(
      temperaturess: Iterable[Iterable[(Location, Temperature)]]
  ): GridLocation => Temperature =
    val grids = temperaturess.map(makeGrid)
    (loc: GridLocation) => grids.map(grid => grid(loc)).sum / grids.size

  /** @param temperatures
    *   Known temperatures
    * @param normals
    *   A grid containing the “normal” temperatures
    * @return
    *   A grid containing the deviations compared to the normal temperatures
    */
  def deviation(
      temperatures: Iterable[(Location, Temperature)],
      normals: GridLocation => Temperature
  ): GridLocation => Temperature =
    println("Creating grid")
    val currentGrid = makeGrid(temperatures)
    (loc: GridLocation) => currentGrid(loc) - normals(loc)
