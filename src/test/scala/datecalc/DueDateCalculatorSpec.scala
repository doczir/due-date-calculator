package datecalc

import java.time.LocalDateTime

import org.scalatest.{FlatSpec, Matchers}

class DueDateCalculatorSpec extends FlatSpec with Matchers {

  "The DueDateCalculator" should "properly calculate the due date when it's the same day as reported" in {
    val submitDate = LocalDateTime.of(2017, 7, 27, 9, 0) // THURSDAY
    val turnaroundTime = 1

    val expected = LocalDateTime.of(2017, 7, 27, 10, 0) // THURSDAY
    val actual = DueDateCalculator.calculateDueDate(submitDate, turnaroundTime)

    actual should equal(expected)
  }

}
