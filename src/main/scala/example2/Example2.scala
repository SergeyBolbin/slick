package example2

import slick.lifted.ProvenShape
import slick.jdbc.H2Profile.api._

import scala.concurrent._
import scala.concurrent.duration.Duration

object Example2 {

  case class Message (text: String, id: Long = 0L)

  class MessageTable(tag: Tag) extends Table[Message](tag, "message") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def text: Rep[String] = column[String]("text")

    override def * : ProvenShape[Message] = (text, id) <> (Message.tupled, Message.unapply)
  }

  private lazy val db = Database.forConfig("dbProperties") //database

  def execute[T](action: DBIO[T]): T = Await.result(db.run(action), Duration.Inf)

  def main(args: Array[String]): Unit = {
    val messages = TableQuery[MessageTable] //table query
    execute(messages.schema.create)
    execute(messages += Message("Text"))
    execute(messages.result).foreach(println)

    db.close()
  }
}
