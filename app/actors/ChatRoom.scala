package actors

import akka.actor.{Actor, ActorRef}
import models.JBDCBlockingCallsModel

import java.time.ZonedDateTime
import tests._

class ChatRoom(roomName:String,var users:Set[String],db:ActorRef) extends Actor{
  import ChatRoom._
  private var lastMessages = JBDCBlockingCallsModel.getLastRoomMessagesFromDB(MaxStoredMessages,roomName)
  private var onlineUsers = Set.empty[ActorRef]
  def handleIncomingMessage(username: String, msg: String): Unit = {
    otpl(s"$username [$roomName]: $msg",0)
    onlineUsers.foreach(user=>
      user ! ChatActor.ToSocketMessage(username,ZonedDateTime.now(),msg)
    )
    db ! BDSystemManager.SaveMessage(username,ZonedDateTime.now(),msg,roomName)
    if (lastMessages.size>MaxStoredMessages)
      lastMessages = lastMessages.tail.appended((username,ZonedDateTime.now(),msg))
    else
      lastMessages = lastMessages.appended((username,ZonedDateTime.now(),msg))
  }

  def handleConnectingUser(user: ActorRef): Unit = {
    onlineUsers = onlineUsers + user
    lastMessages.foreach(msg=>user ! ChatActor.ToSocketMessage(msg._1,msg._2,msg._3))
  }

  override def receive: Receive = {
    case IncomingMessage(username,msg) => handleIncomingMessage(username,msg)
    case KickUser(user) => if(onlineUsers.contains(user)) onlineUsers = onlineUsers - user
    case ConnectUser(user) => handleConnectingUser(user)
  }
}
object ChatRoom{
  val MaxStoredMessages = 10
  case class KickUser(user: ActorRef)
  case class IncomingMessage(username:String,msg:String)
  case class ConnectUser(user: ActorRef)
}
