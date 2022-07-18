# Compat

A tiny _work-in-progress_ library to validate compatability between RPC traits and data classes.
Still not being used. 😄 Expect breaking changes.

Built with [Scalameta](https://github.com/scalameta/scalameta) ❤️

### Limitations:

Since it's a just a syntax parser, it does not know what types represent. It means that it
can't infer compatability between type aliases or refined types. 

Here's a short example where tool would lead to false-positive warning.
We know that `String`, `UserIdV1` and `String Refined NonEmpty` are the same thing transport layer wise, but it's not
possible to easily infer this by looking just at source code AST.

```scala
// Protocol V1
trait Users[F[_]] {
  def get(id: String): F[User]
}

// Protocol V2a
trait Users[F[_]] {
  type UserIdV1 = String
  def get(id: UserIdV1): F[User] 
  // warning: UserIdV1 != String
}

// Protocol V2b
trait Users[F[_]] {
  def get(id: String Refined NonEmpty): F[User] 
  // warning: String Refined NonEmpty != String
}
```
