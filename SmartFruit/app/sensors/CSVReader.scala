package sensors
import java.io.File
import scala.io.Source

case class State(charge: Int, temperature: Int, place: Place)
case class Location(lat: Double, long: Double)
case class Place(name: String, location: Location)
case class Weather(sunshine : Int, temperature : Int , humidity : Int, wind : Int)

class CSVReader
{
  def fileisgood(file: File) = {
    if (!file.exists || file.isDirectory)
      throw new IllegalArgumentException("File doesn't exist.")
  }

  def getState(sensor: String): List[State] = {
    val file = new File(sensor)
    val content: Iterator[Array[String]] = Source.fromFile(sensor).getLines.map(_.split(",")).drop(1)
    content.toList.map(t => State(t(0).toInt, t(1).toInt, Place(t(4), Location(t(2).toDouble, t(3).toDouble))))
  }

  def getWeather(sensor: String): List[Weather] = {
    val file = new File(sensor)
    val content: Iterator[Array[String]] = Source.fromFile(sensor).getLines.map(_.split(",")).drop(1)
    content.toList.map(t => Weather(t(0).toInt, t(1).toInt, t(2).toInt, t(3).toInt))
  }
}
