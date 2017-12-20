package simple

import slick.jdbc.H2Profile.api._


object Profession extends Enumeration {
  type Profession = Value
  val Drummer, Instrumentalist, Singer = Value

  implicit val columnType: BaseColumnType[Profession] =
    MappedColumnType.base[Profession, String](_.toString, Profession.withName)
}
