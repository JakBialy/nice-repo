import cats.data.Kleisli
import cats.effect._
import org.http4s.HttpRoutes
import org.http4s.implicits._
import org.http4s.server.blaze._

import scala.concurrent.ExecutionContext.global
import org.http4s._
import org.http4s.dsl.io._
import services.{BoratService, DzikService, ImageService}

import java.util.concurrent._

object Main extends IOApp {
  val blockingPool: ExecutorService = Executors.newFixedThreadPool(4)
  val blocker: Blocker = Blocker.liftExecutorService(blockingPool)

  val boratService = new BoratService
  val dzikService = new DzikService

  val helloWorldService: Kleisli[IO, Request[IO], Response[IO]] = HttpRoutes.of[IO] {
    case request@GET -> Root / "borat" =>
      fileResponse(request, boratService)
    case request@GET -> Root / "dzik" =>
      fileResponse(request, dzikService)
  }.orNotFound

  private def fileResponse(request: Request[IO], service: ImageService) = {
    StaticFile.fromFile(service.fileProvider, blocker, Some(request))
      .getOrElseF(NotFound())
  }

  def run(args: List[String]): IO[ExitCode] = {
    // heroku is deploying the app on random port
    val port = sys.env.getOrElse("PORT", "8080").toInt
    val host = port match {
      case 8080 => "localhost"
      case _ => "0.0.0.0"
    }

    BlazeServerBuilder[IO](global)
      .bindHttp(port, host)
      .withHttpApp(helloWorldService)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}