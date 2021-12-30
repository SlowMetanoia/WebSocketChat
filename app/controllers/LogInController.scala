package controllers

import play.api.mvc._

import javax.inject.{Inject, Singleton}

@Singleton
class LogInController @Inject()(cc: ControllerComponents)
  extends AbstractController(cc){
  def login() = Action{ implicit request =>
    Ok(views.html.login())
  }
  def validateUser = Action{ implicit request=>
    val formData:Option[Map[String,Seq[String]]] = request.body.asFormUrlEncoded
    val (username,password):(String,String) = formData.map{args=>
      (args("username").head,args("password").head)
    }.getOrElse("wrong","wrong")
    if(models.JBDCBlockingCallsModel.validateUser(username,password)){
      Redirect(routes.WebSocketChat.index).withSession("username"->username).flashing("success"->s"you logged in as $username")
    } else {
      Redirect(routes.LogInController.login).flashing("error"->"wrong username/password")
    }
  }
  def createUser = Action{ implicit request =>
    val formData:Option[Map[String,Seq[String]]] = request.body.asFormUrlEncoded
    val (username,password):(String,String) = formData.map{args=>
      (args("username").head,args("password").head)
    }.getOrElse("wrong","wrong")
    if(models.JBDCBlockingCallsModel.createUser(username,password)){
      Redirect(routes.WebSocketChat.index).withSession("username"->username).flashing("success"->"user created")
    } else {
      Redirect(routes.LogInController.login).flashing("error"->"user already exists")
    }
  }
  def logout() = Action{
    Redirect(routes.LogInController.login).withNewSession
  }
}
