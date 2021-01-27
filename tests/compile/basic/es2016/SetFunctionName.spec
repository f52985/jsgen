        1. Assert: _F_ is an extensible object that does not have a `name` own property.
        1. Assert: Type(_name_) is either Symbol or String.
        1. Assert: If _prefix_ was passed, then Type(_prefix_) is String.
        1. If Type(_name_) is Symbol, then
          1. Let _description_ be _name_'s [[Description]] value.
          1. If _description_ is *undefined*, let _name_ be the empty String.
          1. Else, let _name_ be the concatenation of `"["`, _description_, and `"]"`.
        1. If _prefix_ was passed, then
          1. Let _name_ be the concatenation of _prefix_, code unit 0x0020 (SPACE), and _name_.
        1. Return ! DefinePropertyOrThrow(_F_, `"name"`, PropertyDescriptor{[[Value]]: _name_, [[Writable]]: *false*, [[Enumerable]]: *false*, [[Configurable]]: *true*}).