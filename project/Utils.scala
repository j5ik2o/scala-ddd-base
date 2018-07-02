import sbt._

object Utils {

  implicit class SbtLoggerOps(val self: sbt.Logger) extends AnyVal {
    def toScalaProcessLogger: scala.sys.process.ProcessLogger = new scala.sys.process.ProcessLogger {
      private val _log                     = new FullLogger(self)
      override def out(s: => String): Unit = _log.info(s)

      override def err(s: => String): Unit = _log.err(s)

      override def buffer[T](f: => T): T = _log.buffer(f)
    }
  }

}
