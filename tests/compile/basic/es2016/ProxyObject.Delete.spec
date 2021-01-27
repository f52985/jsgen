        1. Assert: IsPropertyKey(_P_) is *true*.
        1. Let _handler_ be the value of the [[ProxyHandler]] internal slot of _O_.
        1. If _handler_ is *null*, throw a *TypeError* exception.
        1. Assert: Type(_handler_) is Object.
        1. Let _target_ be the value of the [[ProxyTarget]] internal slot of _O_.
        1. Let _trap_ be ? GetMethod(_handler_, `"deleteProperty"`).
        1. If _trap_ is *undefined*, then
          1. Return ? _target_.[[Delete]](_P_).
        1. Let _booleanTrapResult_ be ToBoolean(? Call(_trap_, _handler_, « _target_, _P_ »)).
        1. If _booleanTrapResult_ is *false*, return *false*.
        1. Let _targetDesc_ be ? _target_.[[GetOwnProperty]](_P_).
        1. If _targetDesc_ is *undefined*, return *true*.
        1. If _targetDesc_.[[Configurable]] is *false*, throw a *TypeError* exception.
        1. Return *true*.