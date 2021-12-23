package models


import scalikejdbc.{AutoSession, ConnectionPool, DBSession, scalikejdbcSQLInterpolationImplicitDef}

import java.time.ZonedDateTime

/**
 * Вызовы jdbc, блокирующие поток.
 */
object JBDCBlockingCalls {
  Class.forName("org.postgresql.Driver")
  ConnectionPool.singleton(
    "jdbc:postgresql://localhost:5432/SocChat",
    "SocNetOperator",
    "udhU5o51Abwa")
  implicit val session: DBSession = AutoSession
  def getLastMessagesFromDB(n:Int,roomName:String):List[(String,ZonedDateTime,String)] = {
    sql"SELECT * FROM message WHERE room_name = ${roomName} ORDER BY id DESC LIMIT ${n}".map{rs=>
      (
        rs.string("username"),
        rs.zonedDateTime("sent_time"),
        rs.string("text")
      )
    }.list.apply()
  }
  def validateUser(username:String,password:String): Boolean = {
    sql"SELECT * FROM public.user WHERE (username = ${username}) AND (password = ${password})".map{ _ =>return true}.single.apply()
    false
  }
  def createUser(username:String,password:String):Boolean = {
    sql"SELECT * FROM public.user WHERE username = $username".map(_=>return false).single.apply()
    sql"INSERT INTO public.user (username,password) VALUES ( $username , $password)".update().apply()
    true
  }
}
