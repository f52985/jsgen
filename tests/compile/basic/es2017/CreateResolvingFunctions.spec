          1. Let _alreadyResolved_ be a new Record { [[Value]]: *false* }.
          1. Let _resolve_ be a new built-in function object as defined in Promise Resolve Functions (<emu-xref href="#sec-promise-resolve-functions"></emu-xref>).
          1. Set _resolve_.[[Promise]] to _promise_.
          1. Set _resolve_.[[AlreadyResolved]] to _alreadyResolved_.
          1. Let _reject_ be a new built-in function object as defined in Promise Reject Functions (<emu-xref href="#sec-promise-reject-functions"></emu-xref>).
          1. Set _reject_.[[Promise]] to _promise_.
          1. Set _reject_.[[AlreadyResolved]] to _alreadyResolved_.
          1. Return a new Record { [[Resolve]]: _resolve_, [[Reject]]: _reject_ }.