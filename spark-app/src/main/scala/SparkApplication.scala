import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import java.util.Properties

object SparkApplication {
  def main(args: Array[String]): Unit = {

    val resourceDir = args(0)
    val dbUser = args(1)
    val dbPassWd = args(2)
    val dbUrl = s"jdbc:${args(4)}/${args(3)}"
    val dbConfig = new Properties()
    dbConfig.put("driver", "com.mysql.jdbc.Driver")
    dbConfig.put("user", s"$dbUser")
    dbConfig.put("password", s"$dbPassWd")

    val spark = SparkSession.builder.appName("YANOLJA SPARK APP").getOrCreate()

    // Original DF
    val businessDF = spark.read.json(s"$resourceDir/yelp_academic_dataset_business.json")
    val checkinDF = spark.read.json(s"$resourceDir/yelp_academic_dataset_checkin.json")
    val reviewDF = spark.read.json(s"$resourceDir/yelp_academic_dataset_review.json")
    val tipDF = spark.read.json(s"$resourceDir/yelp_academic_dataset_tip.json")
    val userDF = spark.read.json(s"$resourceDir/yelp_academic_dataset_user.json")


    /**
      * find hip place !!
      */
    mineHipPlace(dbUrl, dbConfig, businessDF, tipDF, userDF, reviewDF)
  }


  /**
    * review data를 database에 dump 한다.
    *
    * @param dbUrl
    * @param dbConfig
    * @param reviewDF
    * @param businessDF
    */
  def dumpReviews(dbUrl: String, dbConfig: Properties,
                  reviewDF: DataFrame, businessDF: DataFrame): Unit = {
    val tempDF = businessDF.withColumnRenamed("business_id", "good-business_id")
    tempDF.join(reviewDF, tempDF("good-business_id") === reviewDF("business_id"))
      .filter(reviewDF.col("date") > "2018-01-01 00:00:00") // 과제임을 가만해 review의 양을 조절함
      .drop("stars")
      .drop("funny")
      .drop("cool")
      .drop("useful")
      .drop("review_id")
      .drop("good-business_id")
      .drop("user_id")
      .drop("address")
      .drop("city")
      .drop("latitude")
      .drop("longitude")
      .drop("name")
      //      .drop("date")
      //      .drop("business_id")
      .withColumnRenamed("text", "review")
      .write.mode(SaveMode.Append).jdbc(dbUrl, "REVIEWS", dbConfig)
  }

  /**
    * tip data를 database에 dump 한다.
    *
    * @param dbUrl
    * @param dbConfig
    * @param tipDF
    * @param businessDF
    */
  def dumpTips(dbUrl: String, dbConfig: Properties,
               tipDF: DataFrame, businessDF: DataFrame): Unit = {
    val tempDF = businessDF.withColumnRenamed("business_id", "good-business_id")
    tempDF.join(tipDF, tempDF("good-business_id") === tipDF("business_id"))
      .filter(tipDF.col("date") > "2018-01-01 00:00:00") // 과제임을 가만해 tip의 양을 조절함
      .drop("compliment_count")
      .drop("user_id")
      .drop("good-business_id")
      .drop("address")
      .drop("city")
      .drop("latitude")
      .drop("longitude")
      .drop("name")
      //      .drop("date")
      //      .drop("business_id")
      .withColumnRenamed("text", "tip")
      .write.mode(SaveMode.Append).jdbc(dbUrl, "TIPS", dbConfig)
  }

  /**
    * find hip place
    *
    * @param dbUrl
    * @param dbConfig
    * @param businessDF
    * @param tipDF
    * @param userDF
    * @param reviewDF
    */
  def mineHipPlace(dbUrl: String, dbConfig: Properties,
                   businessDF: DataFrame, tipDF: DataFrame,
                   userDF: DataFrame, reviewDF: DataFrame): Unit = {

    // 평가가 좋은 가게를 필터링
    val goodBusinessDF = businessDF
      .filter(businessDF.col("stars") > 4) // star 많이 받은 가게
      .filter(businessDF.col("review_count") > 100) // 리뷰가 충분히 있는 가게
      .filter(businessDF.col("is_open") === 1) // 현재 운영 중인 가게
      .drop("is_open")
      .drop("review_count")
      .drop("stars")
      .drop("attributes")
      .drop("categories")
      .drop("hours")
      .drop("postal_code")
      .drop("state")
      .withColumnRenamed("business_id", "business-business_id")
    //      .withColumnRenamed("address", "business-address")
    //      .withColumnRenamed("city", "business-city")
    //      .withColumnRenamed("latitude", "business-latitude")
    //      .withColumnRenamed("longitude", "business-longitude")
    //      .withColumnRenamed("name", "business-name")

    // 최근 리뷰 중 평가가 긍정적인 내용을 포함한 리뷰를 필터링
    val goodLatestReviewDF = reviewDF
      .filter(reviewDF.col("date") > "2018-01-01 00:00:00") // 2018 년 이후 리뷰만
      .filter(reviewDF.col("stars") > 4) // star 많이 맏은 리뷰만
      .filter(reviewDF.col("text").contains("good") || reviewDF.col("text").contains("Good") ||
      reviewDF.col("text").contains("great") || reviewDF.col("text").contains("Great") ||
      reviewDF.col("text").contains("nice") || reviewDF.col("text").contains("Nice") ||
      reviewDF.col("text").contains("cool") || reviewDF.col("text").contains("Cool") ||
      reviewDF.col("text").contains("wonderful") || reviewDF.col("text").contains("impressive") ||
      reviewDF.col("text").contains("fine") || reviewDF.col("text").contains("excellent"))
      .drop("stars")
      .drop("funny")
      .drop("cool")
      .drop("useful")
      .drop("review_id")
      .drop("date")
      .drop("text")
      .withColumnRenamed("business_id", "review-business_id")
      .withColumnRenamed("user_id", "review-user_id") // 뒤에서 조인해서 신뢰할 만한 유저가 쓴건지 봐야지

    // 신뢰할만한 User
    val goodUserDF = userDF
      .filter(userDF.col("average_stars") > 4) // good user
      .filter(userDF.col("fans") > 50) // famous user
      .filter(userDF.col("review_count") > 200) // famous user
      .filter(userDF.col("useful") > 500) // famous user
      .drop("average_stars")
      .drop("fans")
      .drop("friends")
      .drop("review_count")
      .drop("useful")
      .drop("compliment_cool")
      .drop("compliment_cute")
      .drop("compliment_funny")
      .drop("compliment_hot")
      .drop("compliment_list")
      .drop("compliment_more")
      .drop("compliment_note")
      .drop("compliment_photos")
      .drop("compliment_plain")
      .drop("compliment_profile")
      .drop("compliment_writer")
      .drop("cool")
      .drop("elite")
      .drop("funny")
      .drop("name")
      .drop("yelping_since")
      .withColumnRenamed("user_id", "user-user_id")


    // 내용이 좋은 리뷰 중 신뢰할만한 User가 작성한 것만 필터링
    val goodReviewByGoodUserDF = goodUserDF.join(goodLatestReviewDF, goodLatestReviewDF("review-user_id") === goodUserDF("user-user_id"))
      .drop("review-user_id")
      .drop("user-user_id")
      .distinct // 한 가게에 대한 리뷰가 중복되므로 제거

    // 모든 조건이 만족하는 가게의 정보를 추출
    val result = goodReviewByGoodUserDF.join(goodBusinessDF, goodReviewByGoodUserDF("review-business_id") === goodBusinessDF("business-business_id"))
      .drop("review-business_id")
      .withColumnRenamed("business-business_id", "business_id")

    result.write.mode(SaveMode.Append).jdbc(dbUrl, "HIP_PLACE", dbConfig)
    dumpReviews(dbUrl, dbConfig, reviewDF, result)
    dumpTips(dbUrl, dbConfig, tipDF, result)

  }

}
