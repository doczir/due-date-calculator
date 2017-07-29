package datecalc

import java.time.temporal.ChronoUnit
import java.time.{DayOfWeek, Duration, LocalDateTime, LocalTime}

import scala.annotation.tailrec

object DueDateCalculator {

  private val startOfBusinessHours = LocalTime.of(9, 0)
  private val endOfBusinessHours = LocalTime.of(17, 0)
  private lazy val dailyWork =  Duration.ofMinutes(ChronoUnit.MINUTES.between(startOfBusinessHours, endOfBusinessHours))

  def calculateDueDate(submitDate: LocalDateTime, turnaroundTimeHours: Long): LocalDateTime = {
    require(turnaroundTimeHours >= 0, "Turnaround time must be greater than 0")
    require(isWeekday(submitDate), "Submit date must be on a weekday")
    require(isWorkingHour(submitDate.toLocalTime), "Submit time must be between 9AM and 5PM")

    val turnaroundTime = Duration.ofHours(turnaroundTimeHours)

    addDailyWork(submitDate, turnaroundTime)
  }

  private def getNextBusinessDay(day: LocalDateTime): LocalDateTime = {
    val nextBusinessDay = day.toLocalDate.getDayOfWeek match {
      case DayOfWeek.FRIDAY => day.plusDays(3)
      case _ => day.plusDays(1)
    }
    nextBusinessDay.toLocalDate.atTime(startOfBusinessHours)
  }

  @tailrec
  def addDailyWork(startOfDay: LocalDateTime, durationLeft: Duration): LocalDateTime = {
    if (durationLeft.toMinutes == 0) startOfDay
    else {
      val timeLeftOfDay = Duration.ofMinutes(ChronoUnit.MINUTES.between(startOfDay.toLocalTime, endOfBusinessHours))
      if (durationLeft.toMinutes >= timeLeftOfDay.toMinutes) {
        addDailyWork(getNextBusinessDay(startOfDay), durationLeft.minus(timeLeftOfDay))
      } else if (durationLeft.toMinutes > dailyWork.toMinutes) {
        addDailyWork(getNextBusinessDay(startOfDay), durationLeft.minus(dailyWork))
      } else {
        addDailyWork(startOfDay.plus(durationLeft), durationLeft.minus(durationLeft))
      }
    }
  }

  private def isWeekday(day: LocalDateTime): Boolean = day.getDayOfWeek match {
    case DayOfWeek.SATURDAY => false
    case DayOfWeek.SUNDAY => false
    case _ => true
  }

  private def isWorkingHour(time: LocalTime):Boolean =
    !time.isBefore(startOfBusinessHours) && !time.isAfter(endOfBusinessHours)
}
