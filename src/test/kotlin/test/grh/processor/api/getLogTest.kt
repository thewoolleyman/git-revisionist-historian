package test.grh.processor.api

import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import com.thewoolleyweb.grh.git.Commit
import com.thewoolleyweb.grh.git.Log
import com.thewoolleyweb.grh.processor.api.Repo
import com.thewoolleyweb.grh.processor.api.getLog
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class getLogTest : StringSpec() {
  init {
    val log = Log(
      commits = listOf(
        Commit(
          sha = "0db40c2c412984411586641d7c80c5fdd1fcb15c",
          message = "this is the first feature's implementation"
        ),
        Commit(
          sha = "c02af2b43bd7cbcbaed987197ee92bc55c54bada",
          message = "Initial commit"
        )
      )
    )

    val resultString = """
    |{
    |  "data": {
    |    "repository": {
    |      "ref": {
    |        "name": "feature1-start",
    |        "target": {
    |          "history": {
    |            "edges": [
    |              {
    |                "node": {
    |                  "oid": "c02af2b43bd7cbcbaed987197ee92bc55c54bada",
    |                  "message": "Initial commit"
    |                }
    |              },
    |              {
    |                "node": {
    |                  "oid": "0db40c2c412984411586641d7c80c5fdd1fcb15c",
    |                  "message": "this is the first feature's implementation"
    |                }
    |              }
    |            ]
    |          }
    |        }
    |      }
    |    }
    |  }
    |}
    """.trimMargin()

    val stubEndpoint = "http://v4Endpoint"
    val stubRepo = Repo("owner", "name")
    val ref = "solution"
    val mockGetRefHistory: (String, Repo, String) -> Triple<Request, Response, Result<String, Exception>> = { _, _, _ ->
      Triple(Request(), Response(), Result.of(resultString))
    }

    "returns commits in reverse order" {
      //      getLog("solution", stubArgs) shouldBe log
      getLog(stubEndpoint, stubRepo, ref, mockGetRefHistory) shouldBe log
    }
  }
}
