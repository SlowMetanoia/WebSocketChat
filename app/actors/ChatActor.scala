package actors

import akka.actor.{Actor, ActorRef, Props}

import java.time.ZonedDateTime
import tests._

class ChatActor(out:ActorRef,var room: ActorRef, username:String, supervisor: ActorRef) extends Actor{
  room ! ChatRoom.ConnectUser(self)
  import ChatActor._

  def handleFromSocketMessage(fromSocketMessage: String): Unit = {
    otpl(s"chatter: $fromSocketMessage",0)
    room ! ChatRoom.IncomingMessage(username,fromSocketMessage)
  }

  def changeRoom(roomName:String): Unit ={
    room ! ChatRoom.KickUser(self)
    supervisor ! ChatSupervisor.getRoom(roomName,self)
  }

  def handleToSocketMessage(username: String, sent_time: ZonedDateTime, msg: String): Unit = {
    out ! s"[${sent_time.toLocalTime.getHour}:${sent_time.toLocalTime.getMinute}]$username:\n\t$msg"
  }

  override def receive: Receive = {
    case fromSocketMessage: String => handleFromSocketMessage(fromSocketMessage)
    case ChangeRoom(room) => this.room = room
    case ToSocketMessage(username,sent_time,msg) => handleToSocketMessage(username,sent_time,msg)
    case _ => println("unhandled message in chatActor!")
  }
}
object ChatActor {
  def props(out:ActorRef, room:ActorRef, username:String,supervisor:ActorRef): Props =
    Props(new ChatActor(out,room,username,supervisor))

  case class ToSocketMessage(username:String,sent_time:ZonedDateTime,msg:String)
  case class ChangeRoom(newRoomName:ActorRef)
}