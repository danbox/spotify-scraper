package com.github.danbox

import sttp.client3.{HttpURLConnectionBackend, UriContext, basicRequest}
import sttp.model.Uri

class Subreddit(name: String) {

  /**
   * Returns a sequence of tracks - split by artist/track name
   * Current regex follows format used by /r/listentothis
   * and may need to be tweaked to work with other subreddits
   *
   * @return Either containing a sequence of (artist, track name)
   */
  def getTracks: Either[String, Seq[(String, String)]] = basicRequest
    .get(uri())
    .header("User-Agent", "spotify-scraper 0.1")
    .send(HttpURLConnectionBackend())
    .body map {
      ujson.read(_)("data")("children").arr map { child => child("data")("title").str }
    } map {
      _ map{
        postTitle => postTitle.substring(0, postTitle.indexOf('['))
      } map {
        _.split("[-|—|\\--]")
      } filter {
        _.length == 2
      } map {
        case Array(f1, f2) => (f1.trim, f2.trim)
      }
    } map { _.toSeq }

  private def uri(): Uri = uri"https://www.reddit.com/r/$name/hot/.json"
}