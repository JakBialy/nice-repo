import java.io.File
import scala.util.Random

object BoratService {

  def fileProvider: File = {
    val value = Random.between(0, 9)
    val rootPath = "src/main/resources/borat"
    val ending = ".jpg"

    println(s"$rootPath + $value + $ending")

    new File(s"$rootPath$value$ending")
  }

}
