package com.patseev.compat.files.v1

@autoFunctorK
trait UserService[F[_]] {
  def get(id: Int): F[String]
  def find(id: Int): F[Option[String]]
}
