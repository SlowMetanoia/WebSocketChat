package actors

object tests {
  val testing = true
  def onTest(code: => Unit): Unit = if(testing) code
  def otp(msg:String): Unit = onTest(println(msg))
  var testLVLS = Map[Int,Boolean](0->false)
  def onTestL(code: => Unit,lvl:Int = 0) = if (testLVLS(lvl)&&testing) code
  def otpl(msg:String,lvl:Int = 0) = onTestL(println(msg),lvl)
}
