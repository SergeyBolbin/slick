package example4

import slick.jdbc.H2Profile.api._
import slick.lifted.ProvenShape

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Example4 {

  case class User(id: Long, name: String)

  case class Message(text: String, senderId: Long, id: Long = 0L)

  class UserTable(tag: Tag) extends Table[User](tag, "user") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey)

    def name: Rep[String] = column[String]("name")

    override def * : ProvenShape[User] = (id, name).mapTo[User] // <> (User.tupled, User.unapply)
  }

  private lazy val users = TableQuery[UserTable]

  class MessageTable(tag: Tag) extends Table[Message](tag, "message") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def senderId: Rep[Long] = column[Long]("sender_id")

    def text: Rep[String] = column[String]("text")

    def sender = foreignKey("sender_fk", senderId, users)(_.id)

    override def * : ProvenShape[Message] = (text, senderId, id).mapTo[Message]
  }

  private lazy val messages = TableQuery[MessageTable]
  private lazy val db = Database.forConfig("dbProperties")

  def execute[T](action: DBIO[T]): T = Await.result(db.run(action), Duration.Inf)

  def main(args: Array[String]): Unit = {

    val init = DBIO.seq(
      users.schema.create,
      messages.schema.create,
      users ++= Seq(User(1, "User 1"), User(2, "User 2")),
      messages ++= Seq(Message("From User 1", 1), Message("From User 1 Again", 1), Message("From User 2", 2))
    )

    execute(init)

    val monadicJoin = for {
      msg <- messages
      sender <- msg.sender
      if sender.id === 1L
    } yield (sender.name, msg.text)

    execute(monadicJoin.result).foreach(println)

    val monadicJoin2 = for {
      msg <- messages
      user <- users
      if user.id === msg.senderId && user.id === 1L
    } yield (user.name, msg.text)

    execute(monadicJoin2.result).foreach(println)

    val applicativeJoin = messages
      .join(users).on(_.senderId === _.id)
      .filter { case (m, u) => u.id === 1L }
      .map { case (m, u) => (u.name, m.text) }

    execute(applicativeJoin.result).foreach(println)


    val applicativeJoinGroupBy = messages
      .join(users).on(_.senderId === _.id)
      .groupBy { case (_, user) => user.name }
      .map { case (name, group) => (name, group.length) }

    execute(applicativeJoinGroupBy.result).foreach(println)
  }
}
