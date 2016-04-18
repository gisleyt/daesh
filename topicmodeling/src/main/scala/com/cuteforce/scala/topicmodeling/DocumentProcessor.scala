package com.cuteforce.scala.topicmodeling

import com.cuteforce.scala.trie.Trie

/**
  * Created by gisle on 4/17/16.
  */
class DocumentProcessor(languageProcessor : Function[Array[String], Array[String]]) {
  val lexicon = Trie.getDictionary()

  def populateLexicon(document: Document): Unit = {
    languageProcessor(document.content)
      .foreach(x => Trie.inject(lexicon, x))
  }

  def getDictionary(): Map[String, Int] = {
    val numberOfWords = Trie.getWordFrequencies(lexicon).map(_._2).sum
    Trie.getWordFrequencies(lexicon)
    .filter(_._2 / numberOfWords.toDouble < 0.0005)
    .filter(_._2 > 5)
    .map(_._1)
    .zipWithIndex
    .toMap
  }

}
