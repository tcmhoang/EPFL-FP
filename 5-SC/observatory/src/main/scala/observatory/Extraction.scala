package observatory

import java.time.LocalDate
import scala.io.Source
import scala.util.Try

/** 1st milestone: data extraction
  */
object Extraction extends ExtractionInterface:

  /** @param year
    *   Year number
    * @param stationsFile
    *   Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile
    *   Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return
    *   A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(
      year: Year,
      stationsFile: String,
      temperaturesFile: String
  ): Iterable[(LocalDate, Location, Temperature)] =
    def toCelcius(ferenheit: Double): Temperature = (ferenheit - 32) * 5 / 9
    def parseStation(raw: String): Option[(StationID, Location)] =
      raw.split(",") match
        case Array(stn, wban, lat, lon) =>
          Try(
            (StationID(stn, wban), Location(lat.toDouble, lon.toDouble))
          ).toOption
        case _ => None

    def parseTemperature(
        raw: String
    ): Option[(StationID, LocalDate, Temperature)] = ???
    ???

  /** @param records
    *   A sequence containing triplets (date, location, temperature)
    * @return
    *   A sequence containing, for each location, the average temperature over
    *   the year.
    */
  def locationYearlyAverageRecords(
      records: Iterable[(LocalDate, Location, Temperature)]
  ): Iterable[(Location, Temperature)] =
    ???
