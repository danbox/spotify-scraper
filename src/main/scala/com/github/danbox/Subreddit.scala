package com.github.danbox

import sttp.client3.{HttpURLConnectionBackend, UriContext, basicRequest}
import sttp.model.Uri

class Subreddit(name: String) {

  private def uri(): Uri = uri"https://www.reddit.com/r/$name/hot/.json"

  def getTitles: Either[String, Seq[String]] = {
    val request = basicRequest.get(uri()).header("User-Agent", "spotify-scraper 0.1")
    val backend = HttpURLConnectionBackend()
    val response = request.send(backend)

    response.body.map {
      ujson.read(_)("data")("children").arr map { child => child("data")("title").str }
    } map { _.toSeq }
  }
}