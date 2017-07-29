package datecalc

import java.time.temporal.ChronoUnit
import java.time.{Duration, LocalDateTime, LocalTime}

object DueDateCalculator {

  private val startOfBusinessHours = LocalTime.of(9, 0)
  private val endOfBusinessHours = LocalTime.of(17, 0)

  def calculateDueDate(submitDate: LocalDateTime, turnaroundTimeHours: Long): LocalDateTime = {
    var turnaroundTime = Duration.ofHours(turnaroundTimeHours)

    var dueDate = submitDate
    var timeLeftOfDay = Duration.ofMinutes(ChronoUnit.MINUTES.between(dueDate.toLocalTime, endOfBusinessHours))

    while(timeLeftOfDay.getSeconds < turnaroundTime.getSeconds) {
      dueDate = getNextBusinessDay(dueDate)
      turnaroundTime = turnaroundTime.minus(timeLeftOfDay)
      timeLeftOfDay = Duration.ofMinutes(ChronoUnit.MINUTES.between(dueDate.toLocalTime, endOfBusinessHours))
    }

    dueDate.plus(turnaroundTime)
  }

  private def getNextBusinessDay(day: LocalDateTime): LocalDateTime = {
    day.plusDays(1).toLocalDate.atTime(startOfBusinessHours)
  }
}
