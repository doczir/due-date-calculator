package datecalc

import java.time.temporal.ChronoUnit
import java.time.{DayOfWeek, Duration, LocalDateTime, LocalTime}

import scala.annotation.tailrec

/**
  * A utility object to calculate due dates for issues using it's submit date and turnaround time.
  *
  * @author Robert Doczi
  *
  * @see [[datecalc.DueDateCalculator#calculateDueDate]]
  */
object DueDateCalculator {

  private val startOfBusinessHours = LocalTime.of(9, 0)
  private val endOfBusinessHours = LocalTime.of(17, 0)

  /**
    * Calculates the due date based on the submit date and turnaround time (hours). The submit date must be between
    * 9AM (inclusive) and 5PM (exclusive) and it must be a weekday. The turnaround time must not be negative. The method
    * assumes working hours between 9AM and 5PM, ignores holidays and skips through weekends.
    *
    * @param submitDate          the submit date the turnaround time starts from
    * @param turnaroundTimeHours the turnaround time to add to the submit date
    * @return the due date
    */
  def calculateDueDate(submitDate: LocalDateTime, turnaroundTimeHours: Long): LocalDateTime = {
    require(turnaroundTimeHours >= 0, "Turnaround time must be greater than 0")
    require(isWeekday(submitDate), "Submit date must be on a weekday")
    require(isWorkingHour(submitDate.toLocalTime), "Submit time must be between 9AM and 5PM")

    val turnaroundTime = Duration.ofHours(turnaroundTimeHours)

    addBusinessHours(submitDate, turnaroundTime)
  }

  /**
    * Returns the next business day, skipping weekends.
    *
    * @param day the day for which the next business day is calculated
    * @return the next business day
    */
  private def getNextBusinessDay(day: LocalDateTime): LocalDateTime = {
    val nextBusinessDay = day.toLocalDate.getDayOfWeek match {
      case DayOfWeek.FRIDAY => day.plusDays(3)
      case _ => day.plusDays(1)
    }
    nextBusinessDay.toLocalDate.atTime(startOfBusinessHours)
  }

  /**
    * Helper method for adding business hours to a date. Uses recursion. The method assumes working hours between
    * 9AM (inclusive) and 5PM (exclusive), ignores holidays and skips through weekends.
    *
    * @param startOfDay   the submit date the turnaround time starts from
    * @param durationLeft the turnaround time to add to the submit date
    * @return the due date
    */
  @tailrec
  private def addBusinessHours(startOfDay: LocalDateTime, durationLeft: Duration): LocalDateTime = {
    if (durationLeft.toMinutes == 0) startOfDay
    else {
      val timeLeftOfDay = Duration.ofMinutes(ChronoUnit.MINUTES.between(startOfDay.toLocalTime, endOfBusinessHours))
      if (durationLeft.toMinutes >= timeLeftOfDay.toMinutes) {
        addBusinessHours(getNextBusinessDay(startOfDay), durationLeft.minus(timeLeftOfDay))
      } else {
        addBusinessHours(startOfDay.plus(durationLeft), durationLeft.minus(durationLeft))
      }
    }
  }

  /**
    * Determines whether the provided day is a weekday.
    *
    * @param day the day to check if it is a weekday
    * @return whether the provided day is a weekday
    */
  private def isWeekday(day: LocalDateTime): Boolean = day.getDayOfWeek match {
    case DayOfWeek.SATURDAY => false
    case DayOfWeek.SUNDAY => false
    case _ => true
  }

  /**
    * Determines whether the provided time is still work time, i.e. it is between 9AM (inclusive) and 5PM (exclusive)
    *
    * @param time the time to check if it is work time
    * @return whether the provided time is still work time
    */
  private def isWorkingHour(time: LocalTime): Boolean =
    !time.isBefore(startOfBusinessHours) && time.isBefore(endOfBusinessHours)
}
