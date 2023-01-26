/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package repositories

import com.mongodb.client.model.Indexes.ascending
import com.mongodb.client.model.ReturnDocument
import config.AppConfig
import models.UserAnswers
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model._
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import java.time.LocalDateTime
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.SECONDS
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PlaybackRepository @Inject()(
                                    val mongo: MongoComponent,
                                    val config: AppConfig,
                                    implicit val ec: ExecutionContext
                                  )
  extends PlayMongoRepository[UserAnswers](
    mongoComponent = mongo,
    collectionName = "user-answers",
    domainFormat = UserAnswers.formats,
    indexes = Seq(
      IndexModel(
        ascending("updatedAt"),
        IndexOptions()
          .unique(false)
          .name("user-answers-updated-at-index")
          .expireAfter(config.mongoPlaybackTTL, SECONDS)
      ),
      IndexModel(
        ascending("newId"),
        IndexOptions()
          .unique(false)
          .name("internal-id-and-utr-and-sessionId-compound-index")
      )
    ),
    replaceIndexes = config.mongoReplaceIndexes
  ){

  private def selector(internalId: String, identifier: String, sessionId: String): Bson = Filters.eq("newId", s"$internalId-$identifier-$sessionId")

  def get(internalId: String, identifier: String, sessionId: String): Future[Option[UserAnswers]] = {
    val modifier = Updates.set("updatedAt", LocalDateTime.now())

    val updateOption = new FindOneAndUpdateOptions()
      .upsert(false)
      .returnDocument(ReturnDocument.AFTER)

    collection.findOneAndUpdate(selector(internalId, identifier, sessionId), modifier, updateOption).toFutureOption()
  }

  def set(userAnswers: UserAnswers): Future[Boolean] = {
    val updatedObject = userAnswers.copy(updatedAt = LocalDateTime.now)

    val updateOptions = ReplaceOptions().upsert(true)
    collection.replaceOne(selector(userAnswers.internalId, userAnswers.identifier,  userAnswers.sessionId),
      updatedObject,
      updateOptions
    ).headOption().map(_.exists(_.wasAcknowledged()))
  }
}
