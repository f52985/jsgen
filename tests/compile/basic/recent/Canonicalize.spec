            1. If _Unicode_ is *true* and _IgnoreCase_ is *true*, then
              1. If the file CaseFolding.txt of the Unicode Character Database provides a simple or common case folding mapping for _ch_, return the result of applying that mapping to _ch_.
              1. Return _ch_.
            1. If _IgnoreCase_ is *false*, return _ch_.
            1. Assert: _ch_ is a UTF-16 code unit.
            1. Let _cp_ be the code point whose numeric value is that of _ch_.
            1. Let _u_ be the result of toUppercase(« _cp_ »), according to the Unicode Default Case Conversion algorithm.
            1. Let _uStr_ be ! CodePointsToString(_u_).
            1. If _uStr_ does not consist of a single code unit, return _ch_.
            1. Let _cu_ be _uStr_'s single code unit element.
            1. If the numeric value of _ch_ ≥ 128 and the numeric value of _cu_ < 128, return _ch_.
            1. Return _cu_.