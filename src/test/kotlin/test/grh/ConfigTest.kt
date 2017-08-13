package test.grh

import com.thewoolleyweb.grh.config.load
import com.beust.klaxon.JsonObject
import com.beust.klaxon.string
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class ConfigTest: StringSpec() {
    init {
        val jsonText = """
            |{
            |  "remote": "origin",
            |  "branchToRevise": "solution",
            |  "incrementCommits": [
            |    {
            |      "message": "initial commit",
            |      "tags": ["feature1-start"],
            |    },
            |    {
            |      "message": "first feature",
            |      "tags": ["feature1-finish","feature2-start"],
            |      "branches": ["feature1","another-branch"]
            |    }
            |  ]
            |}
        """.trimMargin()
        val rawValue: StringBuilder = StringBuilder(jsonText)

        "works" {
            val grhConfig = load(rawValue)
            grhConfig.remote shouldBe "origin"
        }
    }
}