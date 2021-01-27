            1. Let _targetIndex_ be _start_.
            1. Let _sourceIndex_ be 0.
            1. Repeat, while _sourceIndex_ < _sourceLen_
              1. Let _P_ be ! ToString(_sourceIndex_).
              1. Let _exists_ be ? HasProperty(_source_, _P_).
              1. If _exists_ is *true*, then
                1. Let _element_ be ? Get(_source_, _P_).
                1. If _mapperFunction_ is present, then
                  1. Assert: _thisArg_ is present.
                  1. Set _element_ to ? Call(_mapperFunction_, _thisArg_ , « _element_, _sourceIndex_, _source_ »).
                1. Let _shouldFlatten_ be *false*.
                1. If _depth_ > 0, then
                  1. Set _shouldFlatten_ to ? IsArray(_element_).
                1. If _shouldFlatten_ is *true*, then
                  1. Let _elementLen_ be ? ToLength(? Get(_element_, `"length"`)).
                  1. Set _targetIndex_ to ? FlattenIntoArray(_target_, _element_, _elementLen_, _targetIndex_, _depth_ - 1).
                1. Else,
                  1. If _targetIndex_ ≥ 2<sup>53</sup>-1, throw a *TypeError* exception.
                  1. Perform ? CreateDataPropertyOrThrow(_target_, ! ToString(_targetIndex_), _element_).
                  1. Increase _targetIndex_ by 1.
              1. Increase _sourceIndex_ by 1.
            1. Return _targetIndex_.