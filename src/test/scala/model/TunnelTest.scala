package model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TunnelTest extends AnyFlatSpec with Matchers:
    val tunnel = Tunnel(Position2D(0, 0), Position2D(5, 5), Direction.Up)

    "Tunnel" should "be created correctly" in:
        tunnel shouldBe Tunnel(Position2D(0, 0), Position2D(5, 5), Direction.Up)

    it should "permits the teleport" in:
        tunnel.canTeleport(Direction.Up) shouldBe true

    it should "not permits the teleport" in:
        tunnel.canTeleport(Direction.Down) shouldBe false

