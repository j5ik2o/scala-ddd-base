package com.github.j5ik2o.dddbase.example.repository
import java.util.concurrent.atomic.AtomicLong

object IdGenerator {

  private val atomicLong = new AtomicLong(0L)

  def generateIdValue: Long = atomicLong.getAndIncrement()

}
