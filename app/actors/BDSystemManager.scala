package actors

import akka.actor.Actor
import scalikejdbc.{AutoSession, ConnectionPool}
import scalikejdbc._

import java.time.ZonedDateTime

object BDSystemManager {
  //println(System.getProperty("java.class.path"))
  Class.forName("org.postgresql.Driver")
  ConnectionPool.singleton(
    "jdbc:postgresql://localhost:5432/SocChat",
    "SocNetOperator",
    "udhU5o51Abwa")
  implicit val session = AutoSession
  def apply() = new BDSystemManager
  sealed trait Command
  case class SaveMessage(username:String, sentTime:ZonedDateTime, text:String,roomName:String)extends Command
}

class BDSystemManager extends Actor {
  import BDSystemManager._

  def saveMessage(msg:SaveMessage):Unit = {
    //msg.sentTime.toLocalTime
    sql"""
        INSERT INTO message (username,text,sent_time,room_name)
        VALUES (${msg.username},${msg.text},${msg.sentTime},${msg.roomName})
    """.update.apply()
  }

  override def receive: Receive = {
    case msg:SaveMessage => saveMessage(msg)
    case _ => throw new Exception("wrong BDSysManager request")
  }
}
