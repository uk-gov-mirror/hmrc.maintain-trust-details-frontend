/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.actions

import com.google.inject.Inject
import play.api.mvc.Result
import models.requests.DataRequest

import scala.concurrent.{ExecutionContext, Future}

class FakePlaybackIdentifierAction @Inject()(
                                              implicit val executionContext: ExecutionContext
                                            ) extends PlaybackIdentifierAction {

  override def refine[A](request: DataRequest[A]): Future[Either[Result, DataRequest[A]]] = Future.successful(Right(request))

}