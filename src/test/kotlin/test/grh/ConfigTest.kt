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
            |    },
            |    {
            |      "message": "in-progress feature",
            |      "tags": [],
            |      "branches": []
            |    }
            |  ]
            |}
        """.trimMargin()
    val rawValue: StringBuilder = StringBuilder(jsonText)
    val grhConfig = load(rawValue)

    "remote" {
      grhConfig.remote shouldBe "origin"
    }

    "branchToRevise" {
      grhConfig.branchToRevise shouldBe "solution"
    }

    "incrementCommits" {
      val incrementCommit = grhConfig.incrementCommits[0]
      incrementCommit.message shouldBe "first feature"
      incrementCommit.tags shouldBe listOf("feature1-finish", "feature2-start")
      incrementCommit.branches shouldBe listOf("feature1","another-branch")
    }

    "incrementCommits with empty tags and branches" {
      val emptyIncrementCommit = grhConfig.incrementCommits[1]
      emptyIncrementCommit.message shouldBe "in-progress feature"
      emptyIncrementCommit.tags shouldBe emptyList<String>()
      emptyIncrementCommit.branches shouldBe emptyList<String>()
    }
  }
}
