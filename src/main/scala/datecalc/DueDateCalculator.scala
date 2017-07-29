package datecalc

import java.time.{Duration, LocalDateTime}



object DueDateCalculator {

  def calculateDueDate(submitDate: LocalDateTime, turnaroundTimeHours: Long): LocalDateTime = {
    val turnaroundTime = Duration.ofHours(turnaroundTimeHours)
    submitDate.plus(turnaroundTime)
  }

}
