package controllers

import auth.CustomSession
import dto.request.{UserLoginDTO, UserLogoutDTO, UserRegisterDTO}
import play.api.mvc._
import play.api.libs.json._
import service.UserService
import model.User

import java.time.LocalDateTime
import javax.inject._

@Singleton
class AuthController @Inject()(val controllerComponents: ControllerComponents,
                              val userService: UserService) extends BaseController {


  def register() = Action(parse.json) { implicit request =>
    val registerDTO = request.body.validate[UserRegisterDTO](Json.format[UserRegisterDTO]).get
    userService.add(registerDTO)
    Ok("Success")
  }

  def login() = Action(parse.json) { implicit request =>
    val loginDTO = request.body.validate[UserLoginDTO](Json.format[UserLoginDTO]).get
    val user = userService.getByLogin(loginDTO.login).orNull
    if (user == null) {
      Unauthorized
    } else {
      val session = CustomSession.getSession(loginDTO.login).orNull
      if (session == null || session.expiration.isBefore(LocalDateTime.now())) {
        Ok(CustomSession.generateToken(loginDTO.login))
      } else {
        Ok(session.token)
      }
    }
  }

  def logout() = Action(parse.json) { implicit request =>
    val logoutDTO = request.body.validate[UserLogoutDTO](Json.format[UserLogoutDTO]).get
    val session = CustomSession.getSession(logoutDTO.login).orNull
    if (session != null && session.token.equals(logoutDTO.token) && session.login.equals(logoutDTO.login)) {
      CustomSession.removeSession(logoutDTO.login)
      Ok("Success")
    } else {
      Unauthorized
    }
  }
}
