package test.unit.grh.processor.api

import com.thewoolleyweb.grh.processor.api.Repo
import com.thewoolleyweb.grh.processor.api.parseRepo
import io.kotlintest.matchers.*
import io.kotlintest.specs.StringSpec

class parseRepoTest : StringSpec() {
  init {
    val repo = Repo("owner", "name")

    "works" {
      parseRepo("owner/name") shouldBe repo
    }

    "requires a non-null repo" {
      val e = shouldThrow<RuntimeException> {
        parseRepo(null)
      }
      (e.message ?: "") should haveSubstring("PROCESSOR=api should require REPO")
    }

    "requires a non-blank" {
      val e = shouldThrow<RuntimeException> {
        parseRepo(null)
      }
      (e.message ?: "") should haveSubstring("PROCESSOR=api should require REPO")
    }

    "requires an owner" {
      val e = shouldThrow<RuntimeException> {
        parseRepo("/name")
      }
      (e.message ?: "") should haveSubstring("missing owner or name in REPO")
    }

    "requires a name" {
      val e= shouldThrow<RuntimeException> {
        parseRepo("owner/")
      }
      (e.message ?: "") should haveSubstring("missing owner or name in REPO")
    }
  }
}
