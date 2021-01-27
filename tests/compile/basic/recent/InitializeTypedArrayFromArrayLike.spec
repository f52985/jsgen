            1. Assert: _O_ is an Object that has a [[TypedArrayName]] internal slot.
            1. Let _len_ be ? LengthOfArrayLike(_arrayLike_).
            1. Perform ? AllocateTypedArrayBuffer(_O_, _len_).
            1. Let _k_ be 0.
            1. Repeat, while _k_ < _len_,
              1. Let _Pk_ be ! ToString(𝔽(_k_)).
              1. Let _kValue_ be ? Get(_arrayLike_, _Pk_).
              1. Perform ? Set(_O_, _Pk_, _kValue_, *true*).
              1. Set _k_ to _k_ + 1.