package quotidian.model

import org.specs.Specification

class QuoteSpecs extends Specification {
	def have = addToSusVerb("have")
	"Quote with same values" should have {
		val q1 = new Quote("This is a quote","This is a source","This is some context")
		val q2 = new Quote("This is a quote","This is a source","This is some context")
		"the same hashCode" in {
			q1.hashCode mustEqual q2.hashCode
		}
		"the same String representation" in {
			q1.toString mustEqual q2.toString
		}
		"equality" in {
			q1 mustEqual q2
		}
	}
	"XML representation of Quote" should {
		val q1 = new Quote("This is a quote","This is a source","This is some context")
		val q2xml = <quote><id>0</id><text>This is a quote</text><source>This is a source</source><context>This is some context</context></quote>
		val q2 = Quote(q2xml)
		"equal Quote generated from XML with same values" in {
			Quote(q2xml) mustEqual q1
		}
		"generate a Quote equal to one with the same values" in {
			q2 mustEqual q1
		}
		"generate a Quote with the same hashCode to one with the same values" in {
			q2.hashCode mustEqual q1.hashCode
		}
	}
}
