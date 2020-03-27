package lotos.examples

import cats.effect.{ExitCode, IO, IOApp}
import lotos.internal.model.{Consistency, Gen}
import lotos.internal.testing.TestConfig
import lotos.testing.LotosTest
import lotos.testing.syntax._

/*_*/
object UnsafeHashMapTest extends IOApp {
  val hashMapSpec =
    spec(new UnsafeHashMap)
      .withMethod(method("put").param("key")(Gen.intGen(1)).param("value")(Gen.stringGen(1)))
      .withMethod(method("get").param("key")(Gen.intGen(1)))

  val cfg = TestConfig(parallelism = 2, scenarioLength = 10, scenarioRepetition = 3, scenarioCount = 5)

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- LotosTest.forSpec(hashMapSpec, cfg, Consistency.sequential)
    } yield ExitCode.Success

}
