package test.acceptance.grh

fun jarCmd(arguments: String): String {
  val userDir = System.getProperty("user.dir")
  return "java -jar $userDir/build/libs/git-revisionist-historian-dev.jar $arguments"
}
