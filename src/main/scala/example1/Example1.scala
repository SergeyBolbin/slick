package example1

import slick.jdbc.H2Profile.api._
import slick.lifted.ProvenShape

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object Example1 {

  class MessageTable(tag: Tag) extends Table[(Long, String)](tag, "message") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def text: Rep[String] = column[String]("text")

    override def * : ProvenShape[(Long, String)] = (id, text)
  }

  def main(args: Array[String]): Unit = {
    val db = Database.forConfig("dbProperties") //database
    val messages = TableQuery[MessageTable] //table query

    val setupAction = DBIO.seq( //actions
      messages.schema.create, // create table
      messages += (0L, "test") //insert record
    )

    try {
      val setupFuture = db.run(setupAction)
      Await.ready(setupFuture, Duration.Inf)

      val selectFuture: Future[Seq[(Long, String)]] = db.run(messages.result) //SELECT * FROM MESSAGES
      Await.result(selectFuture, Duration.Inf).foreach(println)

    } finally db.close()
  }
}
