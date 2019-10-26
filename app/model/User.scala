package model

import java.time.ZonedDateTime

case class User(id: Int, email: String, password: String, activationDate: Option[ZonedDateTime], created: ZonedDateTime)
