package datecalc

import java.time.LocalDateTime

import org.scalatest.{FlatSpec, Matchers}

class DueDateCalculatorSpec extends FlatSpec with Matchers {

  "The DueDateCalculator" should "properly calculate the due date when it's the same day as reported" in {
    {
      val submitDate = LocalDateTime.of(2017, 7, 27, 9, 0) // THURSDAY
      val turnaroundTime = 1

      val expected = LocalDateTime.of(2017, 7, 27, 10, 0) // THURSDAY
      val actual = DueDateCalculator.calculateDueDate(submitDate, turnaroundTime)

      actual should equal(expected)
    }

    {
      val submitDate = LocalDateTime.of(2017, 7, 27, 9, 0) // THURSDAY
      val turnaroundTime = 2

      val expected = LocalDateTime.of(2017, 7, 27, 11, 0) // THURSDAY
      val actual = DueDateCalculator.calculateDueDate(submitDate, turnaroundTime)

      actual should equal(expected)
    }

    {
      val submitDate = LocalDateTime.of(2017, 7, 27, 10, 0) // THURSDAY
      val turnaroundTime = 1

      val expected = LocalDateTime.of(2017, 7, 27, 11, 0) // THURSDAY
      val actual = DueDateCalculator.calculateDueDate(submitDate, turnaroundTime)

      actual should equal(expected)
    }
  }

  it should "skip over to next day after work hours" in {
    val submitDate = LocalDateTime.of(2017, 7, 27, 16, 0) // THURSDAY
    val turnaroundTime = 2

    val expected = LocalDateTime.of(2017, 7, 28, 10, 0) // FRIDAY
    val actual = DueDateCalculator.calculateDueDate(submitDate, turnaroundTime)

    actual should equal(expected)
  }

  it should "work across multiple days" in {
    val submitDate = LocalDateTime.of(2017, 7, 26, 9, 0) // WEDNESDAY
    val turnaroundTime = 17

    val expected = LocalDateTime.of(2017, 7, 28, 10, 0) // FRIDAY
    val actual = DueDateCalculator.calculateDueDate(submitDate, turnaroundTime)

    actual should equal(expected)
  }

  it should "skip weekends" in {
    val submitDate = LocalDateTime.of(2017, 7, 28, 16, 0) // FRIDAY
    val turnaroundTime = 2

    val expected = LocalDateTime.of(2017, 7, 31, 10, 0) // MONDAY
    val actual = DueDateCalculator.calculateDueDate(submitDate, turnaroundTime)

    actual should equal(expected)
  }

  it should "work across multiple weeks" in {
    val submitDate = LocalDateTime.of(2017, 7, 28, 16, 0) // FRIDAY
    val turnaroundTime = 48 // 6 days

    val expected = LocalDateTime.of(2017, 8, 7, 16, 0) // MONDAY
    val actual = DueDateCalculator.calculateDueDate(submitDate, turnaroundTime)

    actual should equal(expected)
  }

  it should "throw exception when turnaround time is negative" in {
    val submitDate = LocalDateTime.of(2017, 7, 28, 9, 0) // FRIDAY
    val turnaroundTime = -1

    assertThrows[IllegalArgumentException] {
      DueDateCalculator.calculateDueDate(submitDate, turnaroundTime)
    }
  }

  it should "throw exception when submit date is a weekend" in {
    val submitDate = LocalDateTime.of(2017, 7, 29, 9, 0) // SATURDAY
    val turnaroundTime = 1

    assertThrows[IllegalArgumentException] {
      DueDateCalculator.calculateDueDate(submitDate, turnaroundTime)
    }
  }

  it should "throw exception when submit time is a not between 9AM and 5PM" in {
    val submitDate = LocalDateTime.of(2017, 7, 28, 7, 0) // FRIDAY
    val turnaroundTime = 1

    assertThrows[IllegalArgumentException] {
      DueDateCalculator.calculateDueDate(submitDate, turnaroundTime)
    }
  }

  it should "throw an exception when submit time is exactly 5PM" in {
    val submitDate = LocalDateTime.of(2017, 7, 28, 17, 0) // FRIDAY
    val turnaroundTime = 1

    assertThrows[IllegalArgumentException] {
      DueDateCalculator.calculateDueDate(submitDate, turnaroundTime)
    }
  }
}
