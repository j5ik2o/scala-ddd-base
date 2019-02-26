package com.github.j5ik2o.dddbase.example.repository

trait SpecSupport {

  def sameAs[A](c: Traversable[A], d: Traversable[A]): Boolean = {
    def counts(e: Traversable[A]) = e groupBy identity mapValues (_.size)
    counts(c) == counts(d)
  }

}
