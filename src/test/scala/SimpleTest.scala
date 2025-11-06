import org.scalatest.flatspec.AnyFlatSpec

class SimpleTest extends AnyFlatSpec:
    // a simple test only for testing github action
    it should "be true" in:
        val x = 2
        assert(x == 2)