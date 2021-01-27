          1. Let _S_ be the *this* value.
          1. If Type(_S_) is not Object, throw a *TypeError* exception.
          1. If _S_ does not have a [[SetData]] internal slot, throw a *TypeError* exception.
          1. Let _entries_ be the List that is _S_.[[SetData]].
          1. For each _e_ that is an element of _entries_, do
            1. If _e_ is not ~empty~ and SameValueZero(_e_, _value_) is *true*, then
              1. Return _S_.
          1. If _value_ is *-0*, let _value_ be *+0*.
          1. Append _value_ as the last element of _entries_.
          1. Return _S_.