* If Type(_argument_) is Undefined,
  * Return *NaN*.
* If Type(_argument_) is Null,
  * Return *+0*.
* If Type(_argument_) is Boolean,
  * Return 1 if _argument_ is *true*. Return *+0* if _argument_ is *false*.
* If Type(_argument_) is Number,
  * Return _argument_ (no conversion).
* If Type(_argument_) is String,
  * See grammar and conversion algorithm below.
* If Type(_argument_) is Symbol,
  * Throw a *TypeError* exception.
* If Type(_argument_) is Object,
  1. Let _primValue_ be ? ToPrimitive(_argument_, hint Number).
  1. Return ? ToNumber(_primValue_).