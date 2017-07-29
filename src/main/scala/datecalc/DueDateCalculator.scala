package datecalc

import java.time.temporal.ChronoUnit
import java.time.{Duration, LocalDateTime, LocalTime}

object DueDateCalculator {

  private val startOfBusinessHours = LocalTime.of(9, 0)
  private val endOfBusinessHours = LocalTime.of(17, 0)

  def calculateDueDate(submitDate: LocalDateTime, turnaroundTimeHours: Long): LocalDateTime = {
    var turnaroundTime = Duration.ofHours(turnaroundTimeHours)

    val timeLeftOfSubmitDay = Duration.ofMinutes(ChronoUnit.MINUTES.between(submitDate.toLocalTime, endOfBusinessHours))
    var dueDate = submitDate

    if(timeLeftOfSubmitDay.getSeconds < turnaroundTime.getSeconds) {
      dueDate = getNextBusinessDay(submitDate)
      turnaroundTime = turnaroundTime.minus(timeLeftOfSubmitDay)
    }

    dueDate.plus(turnaroundTime)
  }

  private def getNextBusinessDay(day: LocalDateTime): LocalDateTime = {
    day.plusDays(1).toLocalDate.atTime(startOfBusinessHours)
  }
}
