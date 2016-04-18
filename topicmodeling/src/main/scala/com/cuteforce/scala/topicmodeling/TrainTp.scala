package com.cuteforce.scala.topicmodeling

import java.io.{BufferedWriter, File, FileWriter}

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.clustering.{DistributedLDAModel, EMLDAOptimizer, LDA}

import scala.collection.mutable

object TrainTp {

  val languageProcessor = new Function[Array[String], Array[String]] {
    def apply(input: Array[String]): Array[String] = {
      input.flatMap(_.split(' '))
        .filter(!_.matches("[0-9]"))
        .map(_.toLowerCase())
        .map(_.replaceAll("[^a-zøæå]", ""))
    }
  }

  def main(args: Array[String]) {

    val titleIdMapBuilder = mutable.Map[Long, String]()
    val documentProcessor = new DocumentProcessor(languageProcessor)
    TopicModellingUtils.processDocument(new WikiReaderIter(new File(args(0))), titleIdMapBuilder, documentProcessor)
    val titleIdMap = titleIdMapBuilder.toMap

    val dictionary = documentProcessor.getDictionary()
    val corpus = TopicModellingUtils.getCorpus(new WikiReaderIter(new File(args(0))), dictionary, documentProcessor, languageProcessor)
    println("Done creating corpus of size " + corpus.length)
    val conf = new SparkConf().setAppName("TopicModeling") //.setMaster("local[4]")
    val sc = new SparkContext(conf)

    val rddCorpus = sc.makeRDD(corpus).repartition(corpus.length / 100).cache()
    val numberOfTopics = if (args.length > 1) args(1).toInt else 10
    val maxIterations = if (args.length > 2) args(2).toInt else 50

    val ldaModel = new LDA()
      .setK(numberOfTopics)
      .setOptimizer(new EMLDAOptimizer())
      .setMaxIterations(maxIterations)
      .run(rddCorpus)

    // val reverseDict = (Map() ++ dictionary.map(_.swap))
    val distModel = ldaModel.asInstanceOf[DistributedLDAModel]
    val topSimilarDocument = TopicModellingUtils.top10SimilarDocument(distModel, numberOfTopics, titleIdMap)

    val outputFile = new File(if (args.length > 3) args(3) else "/tmp/tp.txt")
    val bw = new BufferedWriter(new FileWriter(outputFile))

    topSimilarDocument.foreach(x => x.foreach(y => bw.write(y._1 + " " + y._2 + " " + y._3.toString + '\n')))
    bw.close()
  }
}
