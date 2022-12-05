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

object Main extends App:

  if (isWin)
    System.setProperty(
      "hadoop.home.dir",
      System.getProperty("user.dir") + "\\winutils\\hadoop-3.3.1"
    )

  lazy val normals =
    Future { readData(Constants.normalYear until Constants.startYear) }

  lazy val currents =
    Future { readData(Constants.startYear to Constants.endYear) }

  lazy val all =
    Await.result(normals, 15.minutes) ++ Await.result(currents, 15.minutes)

  println("Start generating assets")

  Future {
    val grid = Manipulation.makeGrid(all)
    genPixels(grid, LayerName.Temperatures.id)
  }
    .onComplete((_) => println("Generated Temperatures Images Successfully"))

  Future {
    val grid =
      Manipulation.deviation(
        Await.result(currents, 10.minutes),
        Manipulation.makeGrid(Await.result(normals, 10.minutes))
      )
    genPixels(grid, LayerName.Deviations.id)
    println("Generated Deviation Images Successfully")
  }
    .onComplete((_) => println("Done"))

end Main

def genPixels(grid: GridLocation => Temperature, id: String) =
  println("Generating Tiles")
  for zlv <- 0 to 1
  do
    toGridTile(zlv)
      .map(tile => (Visualization2.visualizeGrid(grid, Color.temp, tile), tile))
      .map((image, tile) =>
        IOOperations
          .writeTo(
            Paths.get(
              s"${Constants.assetPath}/$id/$zlv",
              s"${tile.x}-${tile.y}.png"
            ),
            image
          )
          .foreach((path) => print(path.toString()))
      )

def readData(years: Range): Iterable[(Location, Temperature)] =
  println("Reading .... ")
  years
    .map(yr =>
      Extraction.locateTemperatures(
        yr,
        "/stations.csv",
        s"/$yr.csv"
      )
    )
    .map(Extraction.locationYearlyAverageRecords)
    .foldLeft(List[(Location, Temperature)]())(_ ++ _)

def toGridTile(zlv: Int): ParSeq[Tile] =
  (for
    y <- 0 to zlv
    x <- 0 to zlv
  yield Tile(x, y, zlv)).par
