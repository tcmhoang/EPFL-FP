package calculator

object Polynomial extends PolynomialInterface:
  def computeDelta(
      a: Signal[Double],
      b: Signal[Double],
      c: Signal[Double]
  ): Signal[Double] =
    Signal(Math.pow(b(), 2) - 4 * a() * c())

  def computeSolutions(
      a: Signal[Double],
      b: Signal[Double],
      c: Signal[Double],
      delta: Signal[Double]
  ): Signal[Set[Double]] =
    Signal {
      val del = delta()
      if del < 0 then Set()
      else if del == 0 then Set(-b() / (2 * a()))
      else
        Set(
          (-b() + Math.sqrt(del)) / (2 * a()),
          (-b() - Math.sqrt(del)) / (2 * a())
        )
    }
