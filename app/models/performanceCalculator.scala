package models

import scalikejdbc.{AutoSession, ConnectionPool, DBSession, scalikejdbcSQLInterpolationImplicitDef}

import java.time.ZonedDateTime

object performanceCalculator extends App{
  Class.forName("org.postgresql.Driver")
  ConnectionPool.singleton(
    "jdbc:postgresql://localhost:5432/SocChat",
    "SocNetOperator",
    "udhU5o51Abwa")
  implicit val session: DBSession = AutoSession
  var scopes: Seq[(String, ZonedDateTime, String)] =sql"SELECT * FROM message WHERE (text = 'spamEnd') OR (text = 'spamStart')".map{ rs=>
    (
      rs.string("username"),
      rs.zonedDateTime("sent_time"),
      rs.string("text")
    )
  }.list().apply()
  println(scopes.mkString("\n"))
}
