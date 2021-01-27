          1. Let _propertyNameReference_ be the result of evaluating |Expression|.
          1. Let _propertyNameValue_ be GetValue(_propertyNameReference_).
          1. Let _propertyKey_ be ? ToPropertyKey(_propertyNameValue_).
          1. If the code matched by the syntactic production that is being evaluated is strict mode code, let _strict_ be *true*, else let _strict_ be *false*.
          1. Return ? MakeSuperPropertyReference(_propertyKey_, _strict_).