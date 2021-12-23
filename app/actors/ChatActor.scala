package actors

import akka.actor.{Actor, ActorRef, Props}

import java.time.ZonedDateTime

class ChatActor(out:ActorRef, manager: ActorRef, username:String) extends Actor{
  //val username = "testUser"
  manager ! ChatManager.NewChatter(self)
  import ChatActor._
  def receive = {
    case s:String => manager ! ChatManager.Message(username,s)
    case SendMessage(username,sent_time,msg) =>
      out ! s"[${sent_time.toLocalTime.getHour}:${sent_time.toLocalTime.getMinute}]$username:\n\t$msg"
    case _ => println(s"unhadled message in ChatActor")
  }
}

object ChatActor {
  def props(out:ActorRef, manager:ActorRef, username:String) = Props(new ChatActor(out,manager,username))
  case class SendMessage(username:String,sent_time:ZonedDateTime,msg:String)
}