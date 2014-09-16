package testUtils

import org.qirx.littlespec.Specification
import org.qirx.littlespec.io.Source
import org.qirx.littlespec.macros.Location

trait Example { self: Specification =>

  class Example(implicit location: Location) {

    def withSpecification(body: this.type => FragmentBody) =
      createFragment(Source.codeAtLocation(location), body(this))
  }
}