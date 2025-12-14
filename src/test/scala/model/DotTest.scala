package model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DotTest extends AnyFlatSpec with Matchers:
    val dotBasic = DotBasic(Position2D(0, 0))
    val dotPower = DotPower(Position2D(1, 1))

    "DotBasic" should "be created correctly" in:
        dotBasic shouldBe DotBasic(Position2D(0, 0))

    it should "have the correct score" in:
        dotBasic.score shouldBe 10

    "DotPower" should "be created correctly" in:
        dotPower shouldBe DotPower(Position2D(1, 1))

    it should "have the correct score" in:
        dotPower.score shouldBe 50
