package datecalc

import java.time.temporal.ChronoUnit
import java.time.{Duration, LocalDateTime, LocalTime}

import scala.annotation.tailrec

object DueDateCalculator {

  private val startOfBusinessHours = LocalTime.of(9, 0)
  private val endOfBusinessHours = LocalTime.of(17, 0)
  private lazy val dailyWork =  Duration.ofMinutes(ChronoUnit.MINUTES.between(startOfBusinessHours, endOfBusinessHours))

  def calculateDueDate(submitDate: LocalDateTime, turnaroundTimeHours: Long): LocalDateTime = {
    val turnaroundTime = Duration.ofHours(turnaroundTimeHours)

    addDailyWork(submitDate, turnaroundTime)
  }

  private def getNextBusinessDay(day: LocalDateTime): LocalDateTime = {
    day.plusDays(1).toLocalDate.atTime(startOfBusinessHours)
  }

  @tailrec
  def addDailyWork(startOfDay: LocalDateTime, durationLeft: Duration): LocalDateTime = {
    if (durationLeft.toMinutes == 0) startOfDay
    else {
      val timeLeftOfDay = Duration.ofMinutes(ChronoUnit.MINUTES.between(startOfDay.toLocalTime, endOfBusinessHours))
      if (durationLeft.toMinutes > timeLeftOfDay.toMinutes) {
        addDailyWork(getNextBusinessDay(startOfDay), durationLeft.minus(timeLeftOfDay))
      } else if (durationLeft.toMinutes > dailyWork.toMinutes) {
        addDailyWork(getNextBusinessDay(startOfDay), durationLeft.minus(dailyWork))
      } else {
        addDailyWork(startOfDay.plus(durationLeft), durationLeft.minus(durationLeft))
      }
    }
  }
}
