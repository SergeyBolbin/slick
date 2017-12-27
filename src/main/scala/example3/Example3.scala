package example3

import slick.jdbc.H2Profile.api._
import slick.lifted.ProvenShape

import scala.concurrent._
import scala.concurrent.duration.Duration

object Example3 {
  case class Message (text: String, category: String, size: Long, id: Long = 0L)

  class MessageTable(tag: Tag) extends Table[Message](tag, "message") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def text: Rep[String] = column[String]("text")
    def category: Rep[String] = column[String]("category")
    def size: Rep[Long] = column[Long]("size")

    override def * : ProvenShape[Message] = (text, category, size, id) <> (Message.tupled, Message.unapply)
  }


  private lazy val db = Database.forConfig("dbProperties")
  private val messages = TableQuery[MessageTable]

  def execute[T](action: DBIO[T]): T = Await.result(db.run(action), Duration.Inf)
  def main(args: Array[String]): Unit = {

    execute(messages.schema.create)

    execute(messages ++= Seq(
      Message("Text", "Job", 500L),
      Message("Spam text", "Spam", 300L),
      Message("Other", "Job", 1000L)
    ))

    val query1 = messages.filter(_.category === "Job").sortBy(_.size)
      .map(m => (m.id, m.text)).take(2) //transformed to:
      //SELECT id, text FROM message WHERE category = 'Job' ORDER BY size LIMIT 0, 2

    execute(query1.result).foreach(println)

    val query2 = for {
      message <- messages
      if message.size < 900L
    } yield message

    execute(query2.result).foreach(println)

    db.close()
  }
}