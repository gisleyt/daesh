package com.cuteforce.scala.topicmodeling

import java.io.File

import scala.io.Source

class WikiReaderIter(val file : File) extends Iterator[Document] {
  private var currentIdx = 0
  private val wikiFile = Source.fromFile(file)
  private val lines = wikiFile.getLines()
  private var isClosed = false


  override def hasNext: Boolean = {
    if (!isClosed && lines.hasNext) {
      return true
    } else {
      wikiFile.close()
      isClosed = false
      return false
    }
  }

  override def next(): Document =  {
    val titleLine = lines.next()
    if (!titleLine.startsWith("<doc")) {
      throw new IllegalArgumentException("Invalid format")
    } else {
      val idStart = "<doc id=\"".length
      val idStop = titleLine.substring(idStart).indexOf("\"")
      val id = titleLine.substring(idStart, idStart + idStop)
      val titleStart = titleLine.indexOf("title=\"") + "title=\"".length
      val titleStop = titleLine.substring(titleStart).indexOf("\"")
      val title = titleLine.substring(titleStart, titleStart + titleStop)
      val content = getNextContent(lines)
      return new Document(id.toInt, content, title)
    }
  }

  def getNextContent(lines : Iterator[String]): Array[String] = {
    return lines
      .takeWhile(x => !x.startsWith("</doc"))
      .toList
      .toArray
  }
}
