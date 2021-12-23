package auth

import java.time.LocalDateTime
import java.util.UUID

import scala.collection.mutable

case class CustomSession(token: String, login: String, expiration: LocalDateTime)

object CustomSession {

  private val sessions = mutable.Map.empty[String, CustomSession]

  def getSession(login: String): Option[CustomSession] = {
    sessions.get(login)
  }

  def generateToken(login: String): String = {
    val token = s"$login-token-${UUID.randomUUID().toString}"
    sessions.put(login, CustomSession(token, login, LocalDateTime.now().plusHours(6)))

    token
  }

  def removeSession(login: String): Unit = {
    sessions.remove(login)
  }
}