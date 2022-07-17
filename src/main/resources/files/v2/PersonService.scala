package com.patseev.compat.files.v1

@autoFunctorK
trait PersonService[F[_]] {
//  def list(limit: Int): F[List[Person]]
  def get(id: String): F[Person]
}
