        1. Let _buffer_ be ? ValidateSharedIntegerTypedArray(_typedArray_).
        1. Let _i_ be ? ValidateAtomicAccess(_typedArray_, _index_).
        1. Let _v_ be ? ToInteger(_value_).
        1. Let _arrayTypeName_ be _typedArray_.[[TypedArrayName]].
        1. Let _elementSize_ be the Number value of the Element Size value specified in <emu-xref href="#table-49"></emu-xref> for _arrayTypeName_.
        1. Let _elementType_ be the String value of the Element Type value in <emu-xref href="#table-49"></emu-xref> for _arrayTypeName_.
        1. Let _offset_ be _typedArray_.[[ByteOffset]].
        1. Let _indexedPosition_ be (_i_ × _elementSize_) + _offset_.
        1. Perform SetValueInBuffer(_buffer_, _indexedPosition_, _elementType_, _v_, *true*, `"SeqCst"`).
        1. Return _v_.