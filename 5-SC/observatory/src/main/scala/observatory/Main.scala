package observatory

import com.sksamuel.scrimage.implicits.*
import scala.util.Properties.isWin

import collection.parallel.CollectionConverters.*
import scala.collection.parallel.*

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import java.nio.file.Path
import java.nio.file.Paths

import scala.collection.parallel.ForkJoinTaskSupport
import java.util.concurrent.ForkJoinPool

object Main extends App:

  if (isWin)
    System.setProperty(
      "hadoop.home.dir",
      System.getProperty("user.dir") + "\\winutils\\hadoop-3.3.1"
    )

  val fjts = new ForkJoinTaskSupport(new ForkJoinPool(8))

  val all =
    readData(Constants.startYear to Constants.endYear, fjts)

  println("Start generating assets")

  Future {
    Interaction.generateTiles[Iterable[(Location, Temperature)]](
      all,
      (yr, tile, data) =>
        IOOperations.writeTo(
          Paths.get(
            s"target/temperatures/$yr/${tile.zoom}",
            s"${tile.x}-${tile.y}.png"
          ),
          Visualization2.visualizeGrid(
            Manipulation.makeGrid(data),
            Color.temp,
            tile
          )
        )
    )
  }
    .onComplete((_) => println("Generated Temperatures Images Successfully"))

  Future {
    val normalsAndCurrents =
      all.splitAt((Constants.startYear to Constants.normalYear).size)

    val normals = Manipulation.average(normalsAndCurrents._1.map(_._2))

    Interaction.generateTiles[Iterable[(Location, Temperature)]](
      normalsAndCurrents._2,
      (yr, tile, data) =>
        IOOperations.writeTo(
          Paths.get(
            s"target/deviations/$yr/${tile.zoom}",
            s"${tile.x}-${tile.y}.png"
          ),
          Visualization2.visualizeGrid(
            Manipulation.deviation(data, normals),
            Color.deviations,
            tile
          )
        )
    )
  }
    .onComplete((_) => println("Done"))

end Main

def readData(
    years: Range,
    fyts: ForkJoinTaskSupport
): List[(Year, Iterable[(Location, Temperature)])] =
  println("Reading .... ")
  val yp = years.par
  yp.tasksupport = fyts
  yp
    .map(yr =>
      (
        yr,
        Extraction.locationYearlyAverageRecords(
          Extraction.locateTemperatures(
            yr,
            "/stations.csv",
            s"/$yr.csv"
          )
        )
      )
    )
    .toList
