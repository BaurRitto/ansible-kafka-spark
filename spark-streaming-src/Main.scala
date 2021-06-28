import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.sql._
import org.apache.spark.sql.functions._

object Main {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("kafka-spark-psql")
      .getOrCreate()
    val schema = new StructType()
      .add("postId", IntegerType, false)
      .add("id", IntegerType, false)
      .add("name", StringType, false)
      .add("email", StringType, false)
      .add("body", StringType, false)
      .add("idFromFile", IntegerType, false)
      .add("current_date", DateType, false)

    // to postgresql
    val df = spark.readStream.format("kafka")
      .option("kafka.bootstrap.servers", "kafka:9092")
      .option("subscribe", "comments")
      .load()
      .selectExpr("cast(value as string)")
      .select(from_json(col("value"),schema).as("data"))
      .select("data.*")
      .writeStream
      .foreachBatch(saveToPSQL)
      .start()
  }

  def saveToPSQL = (df: Dataset[Row], batchId: Long) => {
    val url = """jdbc:postgresql://postgresql:5432/spark_data"""

    df
      .write.format("jdbc")
      .option("driver", "org.postgresql.Driver")
      .option("url", url)
      .option("dbtable", "comments_final")
      .option("user", "spark")
      .option("password", "spark")
      .mode("append")
      .save()
  }
}
