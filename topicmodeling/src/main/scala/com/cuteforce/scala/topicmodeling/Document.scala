package com.cuteforce.scala.topicmodeling

import com.cuteforce.scala.trie.Trie


object Document{
  private var current = 0
  private def inc = {current += 1; current}
}


class Document(val id: Long, val content : Array[String], val title : String) {

  def this(content : Array[String], title : String) = this(Document.inc, content, title)

  def getWordVector(dict : Map[String, Int], languageProcessor : Function[Array[String], Array[String]]): Map[Int, Double] = {
    val currentDoc = Trie.getDictionary()
    languageProcessor(this.content)
      .foreach(x => Trie.inject(currentDoc, x))
    return Trie.getWordFrequencies(currentDoc)
      .filter(x => dict.contains(x._1))
      .map(wordFreq => (dict.get(wordFreq._1).get, wordFreq._2.toDouble))
      .toMap
  }
}
