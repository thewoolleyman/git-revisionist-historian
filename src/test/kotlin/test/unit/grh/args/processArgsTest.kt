package test.unit.grh.args

import com.thewoolleyweb.grh.args.processArgs
import com.xenomachina.argparser.InvalidArgumentException
import com.xenomachina.argparser.MissingValueException
import com.xenomachina.argparser.ShowHelpException
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.StringSpec

class processArgsTest : StringSpec() {
  init {
    "can show help" {
      shouldThrow<ShowHelpException> {
        processArgs(arrayOf("--help")) shouldBe "help"
      }
    }

    "can set --processor/-p" {
      processArgs(arrayOf("--processor", "cli")).processor shouldBe "cli"
      processArgs(arrayOf("-p", "cli")).processor shouldBe "cli"
    }

    "supports all valid PROCESSOR values" {
      processArgs(arrayOf("-p", "cli")).processor shouldBe "cli"
      processArgs(arrayOf("-p", "api"), "apiToken").processor shouldBe "api"
    }

    "validates --processor/-p" {
      val e = shouldThrow<InvalidArgumentException> {
        processArgs(arrayOf("--processor", "invalidProcessor"))
      }
      e.message shouldBe ("PROCESSOR must be one of: cli, api")
    }

    "requires --processor/-p" {
      val e = shouldThrow<MissingValueException> {
        processArgs(arrayOf())
      }
      e.message shouldBe ("missing PROCESSOR")
    }

    "can set and default --config/-c" {
      processArgs(arrayOf("--config", "./config.json", "-p", "cli")).configFile shouldBe "./config.json"
      processArgs(arrayOf("-c", "./config.json", "-p", "cli")).configFile shouldBe "./config.json"
      processArgs(arrayOf("-p", "cli")).configFile shouldBe "./grh-config.json"
    }

    "can set and default --dry-run/-n" {
      processArgs(arrayOf("--dry-run", "-p", "cli")).dryRun shouldBe true
      processArgs(arrayOf("-n", "-p", "cli")).dryRun shouldBe true
      processArgs(arrayOf("-p", "cli")).dryRun shouldBe false
    }

    "can set and default --skip-push/-s" {
      processArgs(arrayOf("--skip-push", "-p", "cli")).skipPush shouldBe true
      processArgs(arrayOf("-s", "-p", "cli")).skipPush shouldBe true
      processArgs(arrayOf("-p", "cli")).skipPush shouldBe false
    }

    "only allows --skip-push/-s for --processor=cli" {
      val e = shouldThrow<InvalidArgumentException> {
        processArgs(arrayOf("--skip-push", "-p", "api"))
      }
      e.message shouldBe ("--skip-push option is only valid when --processor=cli")
    }

    "can set --repo/-r" {
      processArgs(arrayOf("--repo", "owner/name", "-p", "api"), "apiToken").repo shouldBe "owner/name"
      processArgs(arrayOf("-r", "owner/name", "-p", "api"), "apiToken").repo shouldBe "owner/name"
    }

    "validates format of provided --repo/-r" {
      val e = shouldThrow<InvalidArgumentException> {
        processArgs(arrayOf("--repo", "invalid format", "-p", "api"))
      }
      e.message shouldBe ("REPO must be a Github repo owner and name separated by slash: 'owner/name'")
    }

    "only allows --repo/-r for --processor=api" {
      val e = shouldThrow<InvalidArgumentException> {
        processArgs(arrayOf("--repo", "owner/name", "-p", "cli"))
      }
      e.message shouldBe ("--repo option is only valid when --processor=api")
    }

    "can set --v-3-endpoint/-3" {
      processArgs(arrayOf("--v-3-endpoint", "http://localhost", "-r", "owner/name", "-p", "api"), "apiToken").v3Endpoint shouldBe "http://localhost"
      processArgs(arrayOf("-3", "http://localhost", "-r", "owner/name", "-p", "api"), "apiToken").v3Endpoint shouldBe "http://localhost"
    }

    "only allows --v-3-endpoint/-3 for --processor=api" {
      val e = shouldThrow<InvalidArgumentException> {
        processArgs(arrayOf("--v-3-endpoint", "http://localhost", "-p", "cli"))
      }
      e.message shouldBe ("--v-3-endpoint option is only valid when --processor=api")
    }

    "can set --v-4-endpoint/-4" {
      processArgs(arrayOf("--v-4-endpoint", "http://localhost", "-r", "owner/name", "-p", "api"), "apiToken").v4Endpoint shouldBe "http://localhost"
      processArgs(arrayOf("-4", "http://localhost", "-r", "owner/name", "-p", "api"), "apiToken").v4Endpoint shouldBe "http://localhost"
    }

    "only allows --v-4-endpoint/-4 for --processor=api" {
      val e = shouldThrow<InvalidArgumentException> {
        processArgs(arrayOf("--v-4-endpoint", "http://localhost", "-p", "cli"))
      }
      e.message shouldBe ("--v-4-endpoint option is only valid when --processor=api")
    }

    "requires GITHUB_PERSONAL_ACCESS_TOKEN for --processor=api" {
      val e = shouldThrow<InvalidArgumentException> {
        processArgs(arrayOf("--v-4-endpoint", "http://localhost", "-p", "api"))
      }
      e.message shouldBe ("GITHUB_PERSONAL_ACCESS_TOKEN env var is required when --processor=api")
    }
  }
}
