package com.cuteforce.scala.topicmodeling

import org.apache.spark.mllib.clustering.{LDAModel, DistributedLDAModel}
import org.apache.spark.mllib.linalg.{Vector, Vectors}

import scala.collection.mutable

/**
  * Created by gisle on 4/18/16.
  */
object TopicModellingUtils {

  def processDocument(reader : Iterator[Document], titleIdMap : mutable.Map[Long, String], documentProcessor : DocumentProcessor): Unit = {
    reader.toStream
      .foreach(document => { documentProcessor.populateLexicon((document));
        titleIdMap.put(document.id, document.title)
      })
  }

  def getCorpus(reader : Iterator[Document], dictionary : Map[String, Int],
                documentProcessor : DocumentProcessor, languageProcessor : Function[Array[String], Array[String]]): Array[(Long, Vector)] = {
    reader
      .toStream
      .map(document => (document.id, Vectors.sparse(dictionary.size, document.getWordVector(dictionary, languageProcessor).toArray)))
      .toArray
  }

  def top10SimilarDocument(distModel: DistributedLDAModel, numberOfTopics : Int, titleIdMap : Map[Long, String]): Array[Array[(String, String, Double)]] = {
    val topicScorePerDocument = distModel.topTopicsPerDocument(numberOfTopics).map { case (id, topics, weights) =>
      val topic = titleIdMap.getOrElse(id, "UnknownDoc")
      (topic, topics.zip(weights).toMap)
    }

    topicScorePerDocument.collect.map { case (topic, topicVector) =>
      topicScorePerDocument
        .map { case (otherTopic, otherTopicVector) =>
          (topic, otherTopic, Math.sqrt(getDistance(topicVector, otherTopicVector)))
        }
        .sortBy(x => x._3)
        .takeOrdered(10)
    }
  }

  def getDistance(topicVector: Map[Int, Double], otherTopicVector: Map[Int, Double]): Double = {
    topicVector
      .toArray
      .map { case (topicId, topicIdWeight) =>
        Math.pow(otherTopicVector.getOrElse(topicId, 0.0) - topicIdWeight, 2)
      }
      .sum
    }


  def printTopicAssignments(distModel: DistributedLDAModel, reverseDict : Map[Int, String], titleIdMap : mutable.Map[Long, String]): Unit = {
    val assignments = distModel.topicAssignments.map { case (id, terms, topicNumber) =>
      val topic = titleIdMap.getOrElse(id, "UnknownDoc")
      val stringTerm = terms.map(idx => reverseDict.getOrElse(idx, "Unknown"))
      (topic, stringTerm.zip(topicNumber))
    }

    assignments.foreach { case (title, topics) =>
      println(title + " " + topics.mkString)
      println("")
    }
  }

  def printHighestWeightedWordsPerTopic(ldaModel: LDAModel, reverseDict : Map[Int, String]): Unit = {
    val topicIndices = ldaModel.describeTopics(maxTermsPerTopic = 10)
    val topics = topicIndices.map { case (terms, termWeights) =>
      val stringTerm = terms.map(idx => reverseDict.getOrElse(idx, "Unknown"))
      stringTerm.zip(termWeights)
    }

    var topicNr = 0
    for (topic <- topics) {
      println("TOPIC " + topicNr)
      println(topic.mkString)
      topicNr += 1
    }
  }
}
