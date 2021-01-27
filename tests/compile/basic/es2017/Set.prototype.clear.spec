          1. Let _S_ be the *this* value.
          1. If Type(_S_) is not Object, throw a *TypeError* exception.
          1. If _S_ does not have a [[SetData]] internal slot, throw a *TypeError* exception.
          1. Let _entries_ be the List that is _S_.[[SetData]].
          1. For each _e_ that is an element of _entries_, do
            1. Replace the element of _entries_ whose value is _e_ with an element whose value is ~empty~.
          1. Return *undefined*.