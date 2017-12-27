package example5

import slick.lifted.ProvenShape

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import slick.jdbc.H2Profile.api._

object Example5 {

  case class Message (text: String, id: Long = 0L)

  class MessageTable(tag: Tag) extends Table[Message](tag, "message") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def text: Rep[String] = column[String]("text")

    override def * : ProvenShape[Message] = (text, id) <> (Message.tupled, Message.unapply)
  }

  private lazy val db = Database.forConfig("dbProperties") //database
  private val messages = TableQuery[MessageTable]

  def execute[T](action: DBIO[T]): T = Await.result(db.run(action), Duration.Inf)

  def main(args: Array[String]): Unit = {
    execute(messages.schema.create)

    val transactional = (
        (messages += Message("Message 1")) >>
        (messages += Message("Message 2")) >>
        (messages += Message("Message 3")) >>
         messages.filter(_.text like "%3").delete >>
         messages.result
    ).transactionally

    execute(transactional).foreach(println)

    val transactionalWithRollback = (
        messages.delete >>
        DBIO.failed(new RuntimeException("Roll back transaction"))
      ).transactionally


    execute(transactionalWithRollback.asTry)
    execute(messages.result).foreach(println)
  }
}
