package actors

import actors.ChatSupervisor._
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import models.JBDCBlockingCalls

class ChatSupervisor(system:ActorSystem,db:ActorRef) extends Actor{
  var rooms: Map[String, ActorRef] = JBDCBlockingCalls.getRooms.map{ values=>
    values._1->
    system.actorOf(Props.create(classOf[ChatRoom], values._1, values._2, db))}

  override def receive: Receive = {
    case ChatSupervisor.SpawnRoom(name) => rooms =
      rooms + (name->system.actorOf(Props.create(classOf[ChatRoom],name,Set.empty[String])))
    case getRoom(name,from) => from ! name
  }
}

object ChatSupervisor{
  case class SpawnRoom(name:String)
  case class AddRoom(name:String, ref: ActorRef)
  case class getRoom(name:String,from:ActorRef)
}