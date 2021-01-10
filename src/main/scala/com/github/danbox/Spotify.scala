package com.github.danbox

import sttp.client3.{HttpURLConnectionBackend, UriContext, basicRequest}
import sttp.model.Uri

import java.net.URLEncoder

class Spotify {

  /**
   * Returns a sequence of Spotify track URIs given a search query
   * There is currently no attempt to filter items after the search has been performed
   *
   * @param q search query
   * @param token API access token
   * @return Either containing the sequence of track URIs as strings
   */
  def searchQuery(q: String, token: String): Either[String, Seq[String]] = {
    basicRequest.get(searchUri(q))
      .header("Authorization", s"Bearer: $token")
      .header("User-Agent", "spotify-scraper 0.1")
      .send(HttpURLConnectionBackend())
      .body map { ujson.read(_)("tracks")("items").arr map { _("href").str }} map { _.toSeq }
  }

  private def searchUri(q: String): Uri =
    uri"https://api.spotify.com/v1/search?q=${URLEncoder.encode(q, "UTF-8")}&type=track"
}
