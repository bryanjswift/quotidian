package example

import org.specs.Specification

object SpecsExample1 extends Specification {
	doAfterSpec {
		Console.println("doAfterSpec")
	}
	"Numbers" should {
		doAfter {
			Console.println("doAfter")
		}
		doLast {
			Console.println("doLast")
		}
		"add" >> {
			2 + 2 mustEqual 4
		}
	}
}

object SpecsExample2 extends Specification {
	doAfterSpec {
		Console.println("doAfterSpec")
	}
	"Numbers" should {
		doAfter {
			Console.println("doAfter")
		}
		doLast {
			Console.println("doLast")
		}
		"add" >> {
			2 + 2 mustEqual 4
		}
		"divide" >> {
			3 / 3 mustEqual 1
		}
	}
}

object SpecsExample3 extends Specification {
	doAfterSpec {
		Console.println("doAfterSpec")
	}
	"Numbers" should {
		doAfter {
			Console.println("doAfter")
		}
		"add" >> {
			2 + 2 mustEqual 4
		}
	}
	"Fun" should {
		doAfter {
			Console.println("doAfter")
		}
		"divide" >> {
			3 / 3 mustEqual 1
		}
	}
}

