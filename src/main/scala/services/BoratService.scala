package services

import java.io.File
import scala.util.Random

class BoratService extends ImageService {

  def fileProvider: File = {
    val value = Random.between(0, 10)
    val rootPath = "src/main/resources/borat/borat"
    val ending = ".jpg"

    println(s"$rootPath + $value + $ending")

    new File(s"$rootPath$value$ending")
  }

}
