package lotos.internal.model

sealed trait TestResult

case object TestSuccess extends TestResult
case class TestFailure(history: List[List[LogEvent]]) extends TestResult
