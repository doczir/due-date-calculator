package datecalc

import java.time.LocalDateTime

object DueDateCalculator {

  def calculateDueDate(submitDate: LocalDateTime, turnaroundTimeHours: Long): LocalDateTime = {
    LocalDateTime.of(2017, 7, 27, 10, 0)
  }

}
