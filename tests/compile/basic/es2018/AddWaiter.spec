          1. Assert: The calling agent is in the critical section for _WL_.
          1. Assert: _W_ is not on the list of waiters in any WaiterList.
          1. Add _W_ to the end of the list of waiters in _WL_.