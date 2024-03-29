package observatory

import java.time.LocalDate
import scala.io.Source
import scala.util.Try
import collection.parallel.CollectionConverters.IterableIsParallelizable
import scala.collection.generic.GenericParCompanion
import scala.collection.parallel.immutable.ParIterable

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.Iterator.apply

/** 1st milestone: data extraction
  */
object Extraction extends ExtractionInterface:

  def toCelcius(ferenheit: Double): Temperature = (ferenheit - 32) * 5 / 9

  def parseStation(raw: String): Option[(StationID, Location)] =
    raw.split(",") match
      case Array(_, _, "", _) | Array(_, _, _, "") =>
        None
      case Array(stn, wban, lat, lon) =>
        Try(
          (StationID(stn, wban), Location(lat.toDouble, lon.toDouble))
        ).toOption
      case _ => None

  var stationCache: Option[
    Map[
      observatory.StationID,
      observatory.Location
    ]
  ] = None

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

    def parseTemperature(
        raw: String
    ): Option[(StationID, LocalDate, Temperature)] =
      raw.split(",") match
        case Array(_, _, _, _, "9999.9") => None
        case Array(stn, wban, month, day, temp) =>
          Try(
            (
              StationID(stn, wban),
              LocalDate.of(year, month.toInt, day.toInt),
              toCelcius(temp.toDouble)
            )
          ).toOption
        case _ => None

    println(s"Reading $year")

    println(s"Reading Station File: ${stationCache == None}")

    val maybeStations =
      if stationCache == None then
        stationCache = IOOperations
          .readData(stationsFile)
          .map(lines =>
            lines
              .map(parseStation)
              .flatten
              .toMap[StationID, Location]
          )
        stationCache
      else stationCache

    Await.result(
      Future {
        IOOperations
          .readData(temperaturesFile)
          .map(lines =>
            lines
              .map(parseTemperature)
              .map(maybeData =>
                maybeData.flatMap(data =>
                  Try(
                    (
                      data._2,
                      maybeStations match
                        case Some(ls) => ls(data._1)
                        case None     => throw new IllegalArgumentException()
                      ,
                      data._3
                    )
                  ).toOption
                )
              )
              .flatten
          )
          .getOrElse(Iterator())
          .to(Iterable)
      },
      15.minutes
    )

  /** @param records
    *   A sequence containing triplets (date, location, temperature)
    * @return
    *   A sequence containing, for each location, the average temperature over
    *   the year.
    */
  def locationYearlyAverageRecords(
      records: Iterable[(LocalDate, Location, Temperature)]
  ): Iterable[(Location, Temperature)] =
    records.par
      .groupBy(_._2)
      .mapValues(ds => ds.map(d => d._3).sum / ds.size)
      .toList
