        1. Assert: IsPropertyKey(_P_) is *true*.
        1. If _argumentsList_ was not passed, let _argumentsList_ be a new empty List.
        1. Let _func_ be ? GetV(_V_, _P_).
        1. Return ? Call(_func_, _V_, _argumentsList_).