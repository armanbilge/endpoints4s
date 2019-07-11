package endpoints.play.server

import endpoints.algebra.Documentation
import play.api.http.{ContentTypes, Writeable}
import play.api.mvc.Results

/**
  * Interpreter for [[endpoints.algebra.JsonEntitiesFromCodec]] that decodes JSON requests
  * and encodes JSON responses.
  *
  * @group interpreters
  */
trait JsonEntitiesFromCodec extends Endpoints with endpoints.algebra.JsonEntitiesFromCodec {

  import playComponents.executionContext

  def jsonRequest[A](docs: Documentation)(implicit codec: JsonCodec[A]): RequestEntity[A] =
    playComponents.playBodyParsers.tolerantText.validate { body =>
      jsonCodecToCodec(codec).decode(body).left.map(ignoredError => Results.BadRequest)
    }

  def jsonResponse[A](docs: Documentation)(implicit codec: JsonCodec[A]): Response[A] = { a =>
    val playCodec = implicitly[play.api.mvc.Codec]
    Results.Ok(playCodec.encode(jsonCodecToCodec(codec).encode(a)))(Writeable(s => s, Some(ContentTypes.JSON)))
  }

}
