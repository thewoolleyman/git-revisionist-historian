package test.grh.config

import com.thewoolleyweb.grh.config.load
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.StringSpec

class loadTest : StringSpec() {
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
            |      "message": "in-progress feature with empty tags and branches",
            |      "tags": [],
            |      "branches": []
            |    }
            |    {
            |      "message": "in-progress feature with omitted tags and branches",
            |      "tags": [],
            |      "branches": []
            |    }
            |  ]
            |}
        """.trimMargin()
    val grhConfig = load(StringBuilder(jsonText))

    "remote" {
      grhConfig.remote shouldBe "origin"
    }

    "branchToRevise" {
      grhConfig.branchToRevise shouldBe "solution"
    }

    "branchToRevise error" {
      val invalidJsonText = """{"remote": "origin","incrementCommits": []}"""
      shouldThrow<IllegalArgumentException> {
        load(StringBuilder(invalidJsonText))
      }
    }

    "incrementCommits" {
      val incrementCommit = grhConfig.incrementCommits[0]
      incrementCommit.message shouldBe "first feature"
      incrementCommit.tags shouldBe listOf("feature1-finish", "feature2-start")
      incrementCommit.branches shouldBe listOf("feature1", "another-branch")
    }

    "incrementCommits with empty tags and branches" {
      val emptyIncrementCommit = grhConfig.incrementCommits[1]
      emptyIncrementCommit.message shouldBe "in-progress feature with empty tags and branches"
      emptyIncrementCommit.tags shouldBe emptyList<String>()
      emptyIncrementCommit.branches shouldBe emptyList<String>()
    }

    "incrementCommits with omitted tags and branches" {
      val omittedIncrementCommit = grhConfig.incrementCommits[2]
      omittedIncrementCommit.message shouldBe "in-progress feature with omitted tags and branches"
      omittedIncrementCommit.tags shouldBe emptyList<String>()
      omittedIncrementCommit.branches shouldBe emptyList<String>()
    }

    "incrementCommits error" {
      val invalidJsonText = """{"remote": "origin","branchToRevise": "solution"}"""
      shouldThrow<IllegalArgumentException> {
        load(StringBuilder(invalidJsonText))
      }
    }
  }
}
