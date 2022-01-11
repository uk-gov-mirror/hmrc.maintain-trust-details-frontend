/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.Configuration
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.WriteConcern
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONSerializationPack
import reactivemongo.api.indexes.Index.Aux
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.play.json.collection.Helpers.idWrites
import models.UserAnswers

import java.time.LocalDateTime
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PlaybackRepositoryImpl @Inject()(
                                        override val mongo: ReactiveMongoApi,
                                        override val config: Configuration
                                      )(override implicit val ec: ExecutionContext)
  extends IndexesManager with PlaybackRepository {

  override val collectionName: String = "user-answers"

  override val cacheTtl: Int = config.get[Int]("mongodb.playback.ttlSeconds")

  override val lastUpdatedIndexName: String = "user-answers-updated-at-index"

  override def idIndex: Aux[BSONSerializationPack.type] = Index.apply(BSONSerializationPack)(
    key = Seq("internalId" -> IndexType.Ascending, "identifier" -> IndexType.Ascending, "newId" -> IndexType.Ascending),
    name = Some("internal-id-and-utr-and-newId-compound-index"),
    expireAfterSeconds = None,
    options = BSONDocument.empty,
    unique = false,
    background = false,
    dropDups = false,
    sparse = false,
    version = None,
    partialFilter = None,
    storageEngine = None,
    weights = None,
    defaultLanguage = None,
    languageOverride = None,
    textIndexVersion = None,
    sphereIndexVersion = None,
    bits = None,
    min = None,
    max = None,
    bucketSize = None,
    collation = None,
    wildcardProjection = None
  )

  private def selector(internalId: String, identifier: String, sessionId: String): JsObject = Json.obj(
    "internalId" -> internalId,
    "identifier" -> identifier,
    "newId" -> s"$internalId-$identifier-$sessionId"
  )

  override def get(internalId: String, identifier: String, sessionId: String): Future[Option[UserAnswers]] = {
    findCollectionAndUpdate[UserAnswers](selector(internalId, identifier, sessionId))
  }

  override def set(userAnswers: UserAnswers): Future[Boolean] = {

    val modifier = Json.obj(
      "$set" -> userAnswers.copy(updatedAt = LocalDateTime.now)
    )

    for {
      col <- collection
      r <- col.update(ordered = false).one(selector(userAnswers.internalId, userAnswers.identifier,  userAnswers.sessionId), modifier, upsert = true, multi = false)
    } yield r.ok
  }

  override def resetCache(internalId: String, identifier: String, sessionId: String): Future[Option[JsObject]] = {
    for {
      col <- collection
      r <- col.findAndRemove(selector(internalId, identifier,sessionId), None, None, WriteConcern.Default, None, None, Seq.empty)
    } yield r.value
  }
}

trait PlaybackRepository {

  def get(internalId: String, identifier: String, sessionId: String): Future[Option[UserAnswers]]

  def set(userAnswers: UserAnswers): Future[Boolean]

  def resetCache(internalId: String, identifier: String, sessionId: String): Future[Option[JsObject]]
}
