package observatory

import com.sksamuel.scrimage.implicits.*
import scala.util.Properties.isWin

import collection.parallel.CollectionConverters.*
import scala.collection.parallel.*

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App:

  if (isWin)
    System.setProperty(
      "hadoop.home.dir",
      System.getProperty("user.dir") + "\\winutils\\hadoop-3.3.1"
    )

    lazy val normals =
      readData(Constants.normalYear until Constants.startYear)

    lazy val currents =
      readData(Constants.startYear to Constants.endYear)

    lazy val all = normals ++ currents

    Future {
      val grid = Manipulation.makeGrid(all)

    }

    Future {

      val grid =
        Manipulation.deviation(currents, Manipulation.makeGrid(normals))

    }

end Main

def genPixels(grid: GridLocation => Temperature) =
  for zlv <- 0 to 3
  do
    toGridTile(zlv).map(tile =>
      Visualization2.visualizeGrid(grid, Color.temp, tile)
    )

def readData(years: Range): Iterable[(Location, Temperature)] =
  years
    .map(yr =>
      Extraction.locateTemperatures(
        yr,
        "/stations.csv",
        s"/$yr.csv"
      )
    )
    .map(Extraction.locationYearlyAverageRecords)
    .reduce(_ ++ _)

def toGridTile(zlv: Int): ParSeq[Tile] =
  (for
    y <- 0 to zlv
    x <- 0 to zlv
  yield Tile(x, y, zlv)).par
