package com.cuteforce.scala.trie

import scala.annotation.tailrec

object Trie {

  final def inject(node: Node, word : String): Unit = {
    if (node.letter.isDefined) {
      throw new IllegalArgumentException("Use the root level of the dictionary")
    } else {
      return inject(node, word.toCharArray)
    }
  }

  @tailrec private[this] final def inject(node: Node, word : Array[Char]): Unit = {
    if (!word.isEmpty) {
      if (!node.daughters.contains(word.head)) {
        node.daughters.put(word.head, new Node(Some(word.head)))
      }
      inject(node.daughters(word.head), word.tail)
    } else {
      node.frequency += 1
    }
  }


  final def getWords(node: Node): Stream[String] = {
    if (node.letter.isDefined) {
      throw new IllegalArgumentException("Use the root level of the dictionary")
    } else {
      return getWords(node, Array.empty)
    }
  }


  private[this] final def getWords(node: Node, prefix: Array[Char]): Stream[String] = {
    if (node.letter.isEmpty) {
      return node.daughters.valuesIterator.flatMap(node => getWords(node, prefix)).toStream
    } else {
      val newPrefix = Array.concat(prefix, Array(node.letter.get))
      if (node.frequency > 0 && node.letter.isDefined) {
        return Stream.cons(newPrefix.mkString, node.daughters.valuesIterator.flatMap(node => getWords(node, newPrefix)).toStream)
      } else {
        return node.daughters.valuesIterator.flatMap(node => getWords(node, newPrefix)).toStream
      }
    }
  }


  final def getWordFrequencies(node: Node): Stream[Tuple2[String, Int]] = {
    if (node.letter.isDefined) {
      throw new IllegalArgumentException("Use the root level of the dictionary")
    } else {
      return getWordFrequencies(node, Array.empty)
    }
  }


  private[this] final def getWordFrequencies(node: Node, prefix: Array[Char]): Stream[Tuple2[String, Int]] = {
    if (node.letter.isEmpty) {
      return node.daughters.valuesIterator.flatMap(node => getWordFrequencies(node, prefix)).toStream
    } else {
      val newPrefix = Array.concat(prefix, Array(node.letter.get))
      if (node.frequency > 0 && node.letter.isDefined) {
        return Stream.cons(new Tuple2(newPrefix.mkString, node.frequency), node.daughters.valuesIterator.flatMap(node => getWordFrequencies(node, newPrefix)).toStream)
      } else {
        return node.daughters.valuesIterator.flatMap(node => getWordFrequencies(node, newPrefix)).toStream
      }
    }
  }


  final def getWordFrequency(node: Node, word : String): Integer = {
    if (node.letter.isDefined) {
      throw new IllegalArgumentException("Use the root level of the dictionary")
    } else {
      return getWordFrequency(node, word.toCharArray)
    }
  }


  @tailrec private[this] final def getWordFrequency(node: Node, word : Array[Char]): Integer = {
    if (word.isEmpty)
      node.frequency
    else {
      if (node.daughters.contains(word.head)) getWordFrequency(node.daughters.get(word.head).get, word.tail) else 0
    }
  }

  final def contains(node: Node, word : String): Boolean = {
    if (node.letter.isDefined) {
      throw new IllegalArgumentException("Use the root level of the dictionary")
    } else {
      return contains(node, word.toCharArray)
    }
  }

  @tailrec private[this] final def contains(node: Node, word : Array[Char]): Boolean = {
    if (word.isEmpty)
      node.frequency > 0
    else {
      if (node.daughters.contains(word.head)) contains(node.daughters.get(word.head).get, word.tail) else false
    }
  }


  final def getDictionary(): Node = {
    new Node()
  }
}
