import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._

import java.io._
import scala.language.implicitConversions
import scala.io.Source

object TestApp {
  def main(args: Array[String]): Unit = {

    case class Country(
                        region: Option[String],
                        area: Double,
                        capital: List[String],
                        name: Name
                      )

    case class Name(official: String)

    case class TargetCountry(
                            name: String,
                            capital: String,
                            area: Double
                            )

    val source = Source.fromFile("data/countries.json").mkString;

    val value = decode[List[Country]](source)

    val result = value match {
      case Right(value) => value
        .filter(x => x.region.contains("Africa") && x.capital.nonEmpty)
        .map(x =>
          TargetCountry(
            name = x.name.official,
            capital = x.capital.head,
            area = x.area
          )
        )
        .sortBy(x => -x.area).take(10)
        .asJson
        .noSpaces
      case Left(value) => throw new RuntimeException(s"Parsing error $value")
    }

    println(result)

    val pw = new PrintWriter(new File(s"data/${args(0)}"))
    pw.write(result)
    pw.close
  }
}
