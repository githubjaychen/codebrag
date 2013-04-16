package com.softwaremill.codebrag.rest

import com.softwaremill.codebrag.AuthenticatableServletSpec
import com.softwaremill.codebrag.service.user.Authenticator
import org.scalatra.auth.Scentry
import com.softwaremill.codebrag.service.data.UserJson
import org.bson.types.ObjectId
import org.mockito.Mockito._
import com.softwaremill.codebrag.dao.reporting.FollowupFinder
import com.softwaremill.codebrag.dao.FollowupDAO

class FollowupsServletSpec extends AuthenticatableServletSpec {

  val currentUser = UserJson(new ObjectId().toString, "user", "user@email.com", "123abc")
  var followupFinder = mock[FollowupFinder]
  var followupDao = mock[FollowupDAO]

  def bindServlet {
    addServlet(new TestableFollowupsServlet(fakeAuthenticator, fakeScentry, followupFinder, followupDao), "/*")
  }

  "GET /" should "call backend for list of followups for authenticated user" in {
    userIsAuthenticatedAs(currentUser)
    get("/") {
      status should be (200)
      verify(followupFinder).findAllFollowupsForUser(new ObjectId(currentUser.id))
    }
  }

}

class TestableFollowupsServlet(fakeAuthenticator: Authenticator, fakeScentry: Scentry[UserJson], followupFinder: FollowupFinder, followupDao: FollowupDAO)
  extends FollowupsServlet(fakeAuthenticator, new CodebragSwagger, followupFinder, followupDao) {
  override def scentry(implicit request: javax.servlet.http.HttpServletRequest) = fakeScentry
}

