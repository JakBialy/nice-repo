import BoratService.fileProvider
import cats.data.Kleisli
import cats.effect._
import org.http4s.HttpRoutes
import org.http4s.implicits._
import org.http4s.server.blaze._

import scala.concurrent.ExecutionContext.global
import org.http4s._
import org.http4s.dsl.io._

object Main extends IOApp {

  import java.util.concurrent._
  val blockingPool = Executors.newFixedThreadPool(4)
  val blocker = Blocker.liftExecutorService(blockingPool)


  val helloWorldService: Kleisli[IO, Request[IO], Response[IO]] = HttpRoutes.of[IO] {
    case request @ GET -> Root / "borat" =>
      StaticFile.fromFile(fileProvider, blocker, Some(request))
        .getOrElseF(NotFound()) // In case the file doesn't exist
  }.orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(helloWorldService)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}