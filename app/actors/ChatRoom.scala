package actors

import akka.actor.{Actor, ActorRef}

import java.time.ZonedDateTime
import tests._

class ChatRoom(roomName:String,var users:Set[String],db:ActorRef) extends Actor{
  import ChatRoom._
  var onlineUsers = Set.empty[ActorRef]

  def handleIncomingMessage(username: String, msg: String): Unit = {
    otpl(s"$username [$roomName]: $msg",0)
    onlineUsers.foreach(user=>
      user ! ChatActor.ToSocketMessage(username,ZonedDateTime.now(),msg)
    )
    db ! BDSystemManager.SaveMessage(username,ZonedDateTime.now(),msg,roomName)
  }

  def handleConnectingUser(user: ActorRef): Unit = {
    onlineUsers = onlineUsers + user
  }

  override def receive: Receive = {
    case IncomingMessage(username,msg) => handleIncomingMessage(username,msg)
    case KickUser(user) => if(onlineUsers.contains(user)) onlineUsers = onlineUsers - user
    case ConnectUser(user) => handleConnectingUser(user)
  }
}
object ChatRoom{
  case class KickUser(user: ActorRef)
  case class IncomingMessage(username:String,msg:String)
  case class ConnectUser(user: ActorRef)
}
