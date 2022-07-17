package com.patseev.compat.files.v1

@autoFunctorK
@deprecated
trait AuthService[F[_]] {
  def singUp(login: String, password: String): F[Unit]

  def login(login: String, password: String): F[Token]
}
