          1. Let _O_ be the *this* value.
          1. Perform ? RequireInternalSlot(_O_, [[ArrayBufferData]]).
          1. If IsSharedArrayBuffer(_O_) is *true*, throw a *TypeError* exception.
          1. If IsDetachedBuffer(_O_) is *true*, return *+0*<sub>𝔽</sub>.
          1. Let _length_ be _O_.[[ArrayBufferByteLength]].
          1. Return 𝔽(_length_).