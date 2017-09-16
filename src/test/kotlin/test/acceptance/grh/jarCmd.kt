package test.acceptance.grh

fun jarCmd(arguments: String): String {
  val userDir = System.getProperty("user.dir")
  // See https://www.pivotaltracker.com/story/show/151146169
  return "java -jar $userDir/build/libs/git-revisionist-historian-dev.jar $arguments"
}
