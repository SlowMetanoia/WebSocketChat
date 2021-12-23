package controllers

import actors.{BDSystemManager, ChatActor, ChatManager}
import akka.actor.{ActorSystem, Props}
import akka.stream.Materializer
import play.api.libs.streams.ActorFlow
import play.api.mvc._

import javax.inject.{Inject, Singleton}

@Singleton
class WebSocketChat @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, mat:Materializer)
  extends AbstractController(cc){
  val DB = system.actorOf(Props[BDSystemManager](),"DB")
  val Manager = system.actorOf(Props.create(classOf[ChatManager],"test manager"),"Manager")
  Manager ! ChatManager.SetDB(DB)

  def index = Action{implicit request =>
    val usernameOption = request.session.get("username")
    usernameOption.map { username=>
      Ok(views.html.chatPage(username))
    }.getOrElse(Redirect(routes.LogInController.login))
  }
  def socket(username:String): WebSocket = WebSocket.accept[String,String] { request =>
    ActorFlow.actorRef { out =>
      ChatActor.props(out, Manager, username)
    }
  }
}