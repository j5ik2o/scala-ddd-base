package example

import org.scalatest.FunSpec

class MoneySpec extends FunSpec {

  describe("Money") {
    it("should be able to sum up") {
      val monies = for (i <- 1 to 1000)
        yield Money.yens(BigDecimal(i))
      val result = monies.sum
      assert(result == Money.yens(500500))
    }
    it("should be able to +, -, *, /") {
      val monies = for (i <- 1 to 1000)
        yield Money.yens(BigDecimal(i))
      val plusTotal    = monies.reduceLeft(_ + _)
      val minusTotal   = monies.reduceLeft(_ - _)
      val timesTotal   = (1 to 10).foldLeft(Money.yens(1))(_ * _)
      val dividedTotal = (1 to 10).foldLeft(Money.yens(100))(_ / _.toDouble)
      assert(plusTotal == Money.yens(500500))
      assert(minusTotal == Money.yens(-500498))
      assert(timesTotal == Money.yens(3628800))
      assert(dividedTotal == Money.yens(0))
    }
  }

}
