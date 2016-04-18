package com.cuteforce.scala.trie

import scala.annotation.tailrec
import scala.collection.mutable

class Node(val letter : Option[Char]) {

  val daughters = mutable.Map[Char, Node]()
  var frequency = 0

  def this() {
    this(None)
  }

  @tailrec final def getNode(prefix : Array[Char]) : Option[Node] = {
    val nodeOption = this.daughters.get(prefix.head)
    if (!nodeOption.isDefined || prefix.tail.isEmpty) {
      return nodeOption
    } else {
      return nodeOption.get.getNode(prefix.tail)
    }
  }
}
