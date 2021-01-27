            1. Assert: IsPromise(_promise_) is *true*.
            1. If _resultCapability_ is not present, then
              1. Set _resultCapability_ to *undefined*.
            1. If IsCallable(_onFulfilled_) is *false*, then
              1. Let _onFulfilledJobCallback_ be ~empty~.
            1. Else,
              1. Let _onFulfilledJobCallback_ be HostMakeJobCallback(_onFulfilled_).
            1. If IsCallable(_onRejected_) is *false*, then
              1. Let _onRejectedJobCallback_ be ~empty~.
            1. Else,
              1. Let _onRejectedJobCallback_ be HostMakeJobCallback(_onRejected_).
            1. Let _fulfillReaction_ be the PromiseReaction { [[Capability]]: _resultCapability_, [[Type]]: ~Fulfill~, [[Handler]]: _onFulfilledJobCallback_ }.
            1. Let _rejectReaction_ be the PromiseReaction { [[Capability]]: _resultCapability_, [[Type]]: ~Reject~, [[Handler]]: _onRejectedJobCallback_ }.
            1. If _promise_.[[PromiseState]] is ~pending~, then
              1. Append _fulfillReaction_ as the last element of the List that is _promise_.[[PromiseFulfillReactions]].
              1. Append _rejectReaction_ as the last element of the List that is _promise_.[[PromiseRejectReactions]].
            1. Else if _promise_.[[PromiseState]] is ~fulfilled~, then
              1. Let _value_ be _promise_.[[PromiseResult]].
              1. Let _fulfillJob_ be NewPromiseReactionJob(_fulfillReaction_, _value_).
              1. Perform HostEnqueuePromiseJob(_fulfillJob_.[[Job]], _fulfillJob_.[[Realm]]).
            1. Else,
              1. Assert: The value of _promise_.[[PromiseState]] is ~rejected~.
              1. Let _reason_ be _promise_.[[PromiseResult]].
              1. If _promise_.[[PromiseIsHandled]] is *false*, perform HostPromiseRejectionTracker(_promise_, *"handle"*).
              1. Let _rejectJob_ be NewPromiseReactionJob(_rejectReaction_, _reason_).
              1. Perform HostEnqueuePromiseJob(_rejectJob_.[[Job]], _rejectJob_.[[Realm]]).
            1. Set _promise_.[[PromiseIsHandled]] to *true*.
            1. If _resultCapability_ is *undefined*, then
              1. Return *undefined*.
            1. Else,
              1. Return _resultCapability_.[[Promise]].