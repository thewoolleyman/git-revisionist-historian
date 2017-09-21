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
            |      "message": "in-progress feature with omitted tags",
            |      "tags": []
            |    },
            |    {
            |      "message": "in-progress feature with empty tags",
            |      "tags": []
            |    },
            |    {
            |      "message": "first feature",
            |      "tags": ["feature1-finish","feature2-start"]
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
      incrementCommit.tags shouldBe listOf("feature1-finish", "feature2-start")
      incrementCommit.message shouldBe "first feature"
    }

    "incrementCommits with empty tags" {
      val emptyIncrementCommit = grhConfig.incrementCommits[1]
      emptyIncrementCommit.message shouldBe "in-progress feature with empty tags"
      emptyIncrementCommit.tags shouldBe emptyList<String>()
    }

    "incrementCommits with omitted tags" {
      val omittedIncrementCommit = grhConfig.incrementCommits[0]
      omittedIncrementCommit.message shouldBe "in-progress feature with omitted tags"
      omittedIncrementCommit.tags shouldBe emptyList<String>()
    }

    "incrementCommits error" {
      val invalidJsonText = """{"remote": "origin","branchToRevise": "solution"}"""
      shouldThrow<IllegalArgumentException> {
        parseConfig(StringBuilder(invalidJsonText))
      }
    }
  }
}
