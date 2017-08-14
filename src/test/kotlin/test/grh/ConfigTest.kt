package test.grh

import com.thewoolleyweb.grh.config.load
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class ConfigTest : StringSpec() {
  init {
    val jsonText = """
            |{
            |  "remote": "origin",
            |  "branchToRevise": "solution",
            |  "incrementCommits": [
            |    {
            |      "message": "first feature",
            |      "tags": ["feature1-finish","feature2-start"],
            |      "branches": ["feature1","another-branch"]
            |    }
            |  ]
            |}
        """.trimMargin()
    val rawValue: StringBuilder = StringBuilder(jsonText)
    val grhConfig = load(rawValue)

    "origin" {
      grhConfig.remote shouldBe "origin"
    }

    "incrementCommits" {
      grhConfig.remote shouldBe "origin"
      val incrementCommits = grhConfig.incrementCommits
      val incrementCommit = incrementCommits[0]
      incrementCommit.message shouldBe "first feature"
      incrementCommit.tags shouldBe listOf("feature1-finish", "feature2-start")
    }
  }
}
