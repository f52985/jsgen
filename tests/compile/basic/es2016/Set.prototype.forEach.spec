          1. Let _S_ be the *this* value.
          1. If Type(_S_) is not Object, throw a *TypeError* exception.
          1. If _S_ does not have a [[SetData]] internal slot, throw a *TypeError* exception.
          1. If IsCallable(_callbackfn_) is *false*, throw a *TypeError* exception.
          1. If _thisArg_ was supplied, let _T_ be _thisArg_; else let _T_ be *undefined*.
          1. Let _entries_ be the List that is the value of _S_'s [[SetData]] internal slot.
          1. Repeat for each _e_ that is an element of _entries_, in original insertion order
            1. If _e_ is not ~empty~, then
              1. Perform ? Call(_callbackfn_, _T_, « _e_, _e_, _S_ »).
          1. Return *undefined*.