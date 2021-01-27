          1. Let _internalSlotsList_ be « [[Prototype]], [[Extensible]], [[ViewedArrayBuffer]], [[TypedArrayName]], [[ContentType]], [[ByteLength]], [[ByteOffset]], [[ArrayLength]] ».
          1. Let _A_ be ! MakeBasicObject(_internalSlotsList_).
          1. Set _A_.[[GetOwnProperty]] as specified in <emu-xref href="#sec-integer-indexed-exotic-objects-getownproperty-p"></emu-xref>.
          1. Set _A_.[[HasProperty]] as specified in <emu-xref href="#sec-integer-indexed-exotic-objects-hasproperty-p"></emu-xref>.
          1. Set _A_.[[DefineOwnProperty]] as specified in <emu-xref href="#sec-integer-indexed-exotic-objects-defineownproperty-p-desc"></emu-xref>.
          1. Set _A_.[[Get]] as specified in <emu-xref href="#sec-integer-indexed-exotic-objects-get-p-receiver"></emu-xref>.
          1. Set _A_.[[Set]] as specified in <emu-xref href="#sec-integer-indexed-exotic-objects-set-p-v-receiver"></emu-xref>.
          1. Set _A_.[[Delete]] as specified in <emu-xref href="#sec-integer-indexed-exotic-objects-delete-p"></emu-xref>.
          1. Set _A_.[[OwnPropertyKeys]] as specified in <emu-xref href="#sec-integer-indexed-exotic-objects-ownpropertykeys"></emu-xref>.
          1. Set _A_.[[Prototype]] to _prototype_.
          1. Return _A_.