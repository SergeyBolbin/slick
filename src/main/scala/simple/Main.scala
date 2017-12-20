package simple

import simple.Profession.{Drummer, Instrumentalist, Profession, Singer}
import slick.jdbc.H2Profile.api._
import slick.lifted.ProvenShape

import scala.concurrent._
import scala.concurrent.duration._

object Main {

  case class Person(
                     id: Long = 0L,
                     name: String,
                     year: Long,
                     profession: Profession
                   )

  class PersonTable(tag: Tag) extends Table[Person](tag, "person") {
    def name: Rep[String] = column[String]("name")
    def year: Rep[Long] = column[Long]("yearOfBirth")
    def profession: Rep[Profession] = column[Profession]("profession")
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def * : ProvenShape[Person] = (id, name, year, profession) <> (Person.tupled, Person.unapply)
  }

  private val PersonTableQuery = TableQuery[PersonTable]

  private val db = Database.forConfig("dbProperties")

  private val createTableAction = PersonTableQuery.schema.create

  private val insertAction = PersonTableQuery ++= Seq(
    Person(name = "John Lennon", year = 1940, profession = Singer),
    Person(name = "Paul Mccartney", year = 1942, profession = Instrumentalist),
    Person(name = "George Harrison", year = 1943, profession = Instrumentalist),
    Person(name = "Ringo Starr", year = 1940, profession = Drummer)
  )

  private val selectAction = PersonTableQuery.result

  def execute[T](action: DBIO[T]): T = Await.result(db.run(action), Duration.Inf)


  def main(args: Array[String]): Unit = {
    execute(createTableAction)
    execute(insertAction)
    execute(selectAction).foreach(println)
  }

}