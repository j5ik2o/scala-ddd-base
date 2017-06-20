package example

import java.util.{Locale, Currency}

final class Money(val amount: BigDecimal, val currency: Currency)
  extends Ordered[Money]
    with Serializable {

  require(amount.scale == currency.getDefaultFractionDigits,
    "Scale of amount does not match currency")

  lazy val abs: Money = Money(amount.abs, currency)

  override def compare(that: Money): Int = {
    require(currency == that.currency)
    amount compare that.amount
  }

  def /(divisor: Double): Money = dividedBy(divisor)

  def *(other: BigDecimal): Money = times(other)

  def +(other: Money): Money = plus(other)

  def -(other: Money): Money = minus(other)

  def unary_-() = negated

  def dividedBy(divisor: Double): Money =
    dividedBy(divisor, Money.DefaultRoundingMode)

  def dividedBy(divisor: BigDecimal, roundingMode: BigDecimal.RoundingMode.Value): Money = {
    val newAmount =
      amount.bigDecimal.divide(divisor.bigDecimal, roundingMode.id)
    Money(BigDecimal(newAmount), currency)
  }

  def dividedBy(divisor: Double, roundingMode: BigDecimal.RoundingMode.Value): Money =
    dividedBy(BigDecimal(divisor), roundingMode)

  def isGreaterThan(other: Money): Boolean =
    this > other

  def isLessThan(other: Money): Boolean = this < other

  lazy val isNegative: Boolean = amount < BigDecimal(0)

  lazy val isPositive: Boolean = amount > BigDecimal(0)

  lazy val isZero: Boolean =
    equals(Money.adjustBy(0.0, currency))

  def minus(other: Money): Money =
    plus(other.negated)

  lazy val negated: Money =
    Money(BigDecimal(amount.bigDecimal.negate), currency)

  def plus(other: Money): Money = {
    checkHasSameCurrencyAs(other)
    Money.adjustBy(amount + other.amount, currency)
  }

  def times(factor: BigDecimal): Money = times(factor, Money.DefaultRoundingMode)

  def times(factor: BigDecimal, roundingMode: BigDecimal.RoundingMode.Value): Money =
    Money.adjustBy(amount * factor, currency, roundingMode)

  def times(amount: Double): Money =
    times(BigDecimal(amount))

  def times(amount: Double, roundingMode: BigDecimal.RoundingMode.Value): Money =
    times(BigDecimal(amount), roundingMode)

  def times(amount: Int): Money =
    times(BigDecimal(amount))

  override def toString: String =
    currency.getSymbol + " " + amount

  def toString(localeOption: Option[Locale]): String = {
    def createStrng(_locale: Locale) =
      currency.getSymbol(_locale) + " " + amount
    localeOption match {
      case Some(locale) => createStrng(locale)
      case None         => createStrng(Locale.getDefault)
    }
  }

  private[example] def hasSameCurrencyAs(arg: Money): Boolean =
    currency.equals(arg.currency) || arg.amount.equals(BigDecimal(0)) || amount
      .equals(BigDecimal(0))

  private[example] lazy val incremented: Money = plus(minimumIncrement)

  private[example] lazy val minimumIncrement: Money = {
    val increment =
      BigDecimal(1).bigDecimal.movePointLeft(currency.getDefaultFractionDigits)
    Money(BigDecimal(increment), currency)
  }

  private def checkHasSameCurrencyAs(aMoney: Money): Unit = {
    if (!hasSameCurrencyAs(aMoney)) {
      throw new ClassCastException(
        aMoney.toString() + " is not same currency as " + this.toString())
    }
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[Money]

  override def equals(other: Any): Boolean = other match {
    case that: Money =>
      (that canEqual this) &&
        amount == that.amount &&
        currency == that.currency
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(amount, currency)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

}

object Money {

  val USD = Currency.getInstance("USD")

  val EUR = Currency.getInstance("EUR")

  val JPY = Currency.getInstance("JPY")

  val DefaultRoundingMode = BigDecimal.RoundingMode.HALF_EVEN

  def apply(amount: Int): Money = apply(BigDecimal(amount))

  def apply(amount: BigDecimal): Money = apply(amount, Currency.getInstance(Locale.getDefault))

  def apply(amount: BigDecimal, currency: Currency): Money =
    adjustBy(amount, currency)

  def unapply(money: Money): Option[(BigDecimal, Currency)] =
    Some((money.amount, money.currency))

  def dollars(amount: BigDecimal): Money = adjustBy(amount, USD)

  def dollars(amount: Double): Money = adjustBy(amount, USD)

  def euros(amount: BigDecimal): Money = adjustBy(amount, EUR)

  def euros(amount: Double): Money = adjustBy(amount, EUR)

  def sum(monies: Iterable[Money])(implicit num: Numeric[Money]): Money = monies.sum

  def adjustBy(amount: BigDecimal, currency: Currency): Money =
    adjustBy(amount, currency, BigDecimal.RoundingMode.UNNECESSARY)

  def adjustBy(rawAmount: BigDecimal,
               currency: Currency,
               roundingMode: BigDecimal.RoundingMode.Value): Money = {
    val amount =
      rawAmount.setScale(currency.getDefaultFractionDigits, roundingMode)
    new Money(amount, currency)
  }

  def adjustBy(dblAmount: Double, currency: Currency): Money =
    adjustBy(dblAmount, currency, DefaultRoundingMode)

  def adjustRound(dblAmount: Double,
                  currency: Currency,
                  roundingMode: BigDecimal.RoundingMode.Value): Money = {
    val rawAmount = BigDecimal(dblAmount)
    adjustBy(rawAmount, currency, roundingMode)
  }

  def yens(amount: BigDecimal): Money = adjustBy(amount, JPY)

  def yens(amount: Double): Money = adjustBy(amount, JPY)

  def zero(currency: Currency): Money = adjustBy(0.0, currency)

  implicit val moneyNumeric = new Numeric[Money] {
    override def plus(x: Money, y: Money): Money = x + y

    override def minus(x: Money, y: Money): Money = x - y

    override def times(x: Money, y: Money): Money = x * y

    override def negate(x: Money): Money = -x

    override def fromInt(x: Int): Money = apply(x)

    override def toInt(x: Money): Int = x.amount.toInt

    override def toLong(x: Money): Long = x.amount.toLong

    override def toFloat(x: Money): Float = x.amount.toFloat

    override def toDouble(x: Money): Double = x.amount.toDouble

    override def compare(x: Money, y: Money): Int = x compare y
  }

}
