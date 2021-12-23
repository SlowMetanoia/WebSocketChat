package actors

import akka.actor.{Actor, ActorRef}
import models.JBDCBlockingCalls

import java.time.ZonedDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ChatManager{
  case class NewChatter(chatter:ActorRef)
  case class Message(username:String,msg:String)
  case class SetDB(db:ActorRef)
  val MaxSavedMessages = 10
}



class ChatManager(roomName:String) extends Actor{
  private var chatters = List.empty[ActorRef]
  private var lastMessages = List.empty[(String,ZonedDateTime,String)]
  private var DataBase:Option[ActorRef] = None
  import ChatManager._

  def handleMessage(username:String,msg: String): Unit = {
    chatters.foreach(_ ! ChatActor.SendMessage(username,ZonedDateTime.now(),msg))
    DataBase.foreach(_ ! BDSystemManager.SaveMessage(username,ZonedDateTime.now(),msg,roomName))
    if(lastMessages.length < MaxSavedMessages)
      lastMessages = lastMessages.appended((username,ZonedDateTime.now(),msg))
      else
      lastMessages = lastMessages.tail.appended((username,ZonedDateTime.now(),msg))
  }

  def handleChatter(chatter: ActorRef): Unit = Future{
    chatters ::= chatter
    lastMessages.foreach{
      msg=>chatter ! ChatActor.SendMessage(msg._1,msg._2,msg._3)
    }
    //println(lastMessages.mkString("\n"))
  }

  def handleDB(db: ActorRef): Unit = {
    DataBase = Some(db)
    lastMessages = JBDCBlockingCalls.getLastMessagesFromDB(MaxSavedMessages,roomName).reverse
  }

  def receive: Receive = {
    case s:String => println(s"Got message $s")
    case NewChatter(chatter) => handleChatter(chatter)
    case Message(username,msg) => handleMessage(username,msg)
    case SetDB(db) => handleDB(db)
    case _ => println(s"unhandled message in ChatManager")
  }
}
