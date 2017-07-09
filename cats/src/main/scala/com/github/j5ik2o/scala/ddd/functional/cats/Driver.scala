package com.github.j5ik2o.scala.ddd.functional.cats

import com.github.j5ik2o.scala.ddd.functional.{ AggregateDeletable, AggregateRepository }

trait Driver extends AggregateRepository with AggregateDeletable {}
