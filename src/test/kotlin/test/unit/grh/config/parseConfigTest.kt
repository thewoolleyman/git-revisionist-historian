package test.unit.grh.config

import com.thewoolleyweb.grh.config.parseConfig
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.StringSpec

class parseConfigTest : StringSpec() {
  init {
    val jsonText = """
            |{
            |  "remote": "origin",
            |  "branchToRevise": "solution",
            |  "incrementCommits": [
            |    {
            |      "message": "in-progress feature with omitted tags and branches",
            |      "tags": [],
            |      "branches": []
            |    },
            |    {
            |      "message": "in-progress feature with empty tags and branches",
            |      "tags": [],
            |      "branches": []
            |    },
            |    {
            |      "message": "first feature",
            |      "tags": ["feature1-finish","feature2-start"],
            |      "branches": ["feature1","another-branch"]
            |    }
            |  ]
            |}
        """.trimMargin()
    val grhConfig = parseConfig(StringBuilder(jsonText))

    "branchToRevise" {
      grhConfig.branchToRevise shouldBe "solution"
    }

    "branchToRevise error" {
      val invalidJsonText = """{"remote": "origin","incrementCommits": []}"""
      shouldThrow<IllegalArgumentException> {
        parseConfig(StringBuilder(invalidJsonText))
      }
    }

    "incrementCommits" {
      val incrementCommit = grhConfig.incrementCommits[2]
      incrementCommit.branches shouldBe listOf("feature1", "another-branch")
      incrementCommit.tags shouldBe listOf("feature1-finish", "feature2-start")
      incrementCommit.message shouldBe "first feature"
    }

    "incrementCommits with empty tags and branches" {
      val emptyIncrementCommit = grhConfig.incrementCommits[1]
      emptyIncrementCommit.message shouldBe "in-progress feature with empty tags and branches"
      emptyIncrementCommit.tags shouldBe emptyList<String>()
      emptyIncrementCommit.branches shouldBe emptyList<String>()
    }

    "incrementCommits with omitted tags and branches" {
      val omittedIncrementCommit = grhConfig.incrementCommits[0]
      omittedIncrementCommit.message shouldBe "in-progress feature with omitted tags and branches"
      omittedIncrementCommit.tags shouldBe emptyList<String>()
      omittedIncrementCommit.branches shouldBe emptyList<String>()
    }

    "incrementCommits error" {
      val invalidJsonText = """{"remote": "origin","branchToRevise": "solution"}"""
      shouldThrow<IllegalArgumentException> {
        parseConfig(StringBuilder(invalidJsonText))
      }
    }
  }
}
