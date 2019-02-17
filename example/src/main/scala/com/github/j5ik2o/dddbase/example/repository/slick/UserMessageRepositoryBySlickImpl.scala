package com.github.j5ik2o.dddbase.example.repository.slick

import com.github.j5ik2o.dddbase.slick._
import slick.jdbc.JdbcProfile

class UserMessageRepositoryBySlickImpl(override val profile: JdbcProfile, override val db: JdbcProfile#Backend#Database)
    extends AbstractUserMessageRepositoryBySlick(profile, db)
    with AggregateSingleSoftDeleteFeature
    with AggregateMultiSoftDeleteFeature
