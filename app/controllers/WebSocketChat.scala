package controllers

import actors.{BDSystemManager, ChatActor, ChatRoom, ChatSupervisor}
import akka.actor.{ActorSystem, Props}
import akka.stream.Materializer
import play.api.libs.streams.ActorFlow
import play.api.mvc._

import javax.inject.{Inject, Singleton}

@Singleton
class WebSocketChat @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, mat:Materializer)
  extends AbstractController(cc){
  val DB = system.actorOf(Props[BDSystemManager]())
  val Supervisor = system.actorOf(Props.create(classOf[ChatSupervisor],system,DB))
  val startRoom = system.actorOf(Props.create(classOf[ChatRoom],"start_room",Set.empty[String],DB))
  Supervisor ! ChatSupervisor.AddRoom("start_room",startRoom)
  def index = Action { implicit request =>
    val usernameOption = request.session.get("username")
    usernameOption.map { username=>
      Ok(views.html.chatPage(username))
    }.getOrElse(Redirect(routes.LogInController.login))
  }

  def socket(username:String): WebSocket = WebSocket.accept[String,String] { request =>
    ActorFlow.actorRef { out =>
      ChatActor.props(out, startRoom, username, Supervisor)
    }
  }
}