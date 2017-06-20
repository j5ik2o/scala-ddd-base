package example

object MoneyMain extends App {
  val monies = for (i <- 1 to 1000)
    yield Money.yens(BigDecimal(i))
  val plusTotal    = monies.reduceLeft(_ + _)
  val minusTotal   = monies.reduceLeft(_ - _)
  val timesTotal   = (1 to 10).foldLeft(Money.yens(1))(_ * _)
  val dividedTotal = (1 to 10).foldLeft(Money.yens(100))(_ / _.toDouble)
  println((plusTotal, minusTotal, timesTotal, dividedTotal))
}