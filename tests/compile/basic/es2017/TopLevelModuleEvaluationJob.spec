          1. Assert: _sourceText_ is an ECMAScript source text (see clause <emu-xref href="#sec-ecmascript-language-source-code"></emu-xref>).
          1. Let _realm_ be the current Realm Record.
          1. Let _m_ be ParseModule(_sourceText_, _realm_, _hostDefined_).
          1. If _m_ is a List of errors, then
            1. Perform HostReportErrors(_m_).
            1. Return NormalCompletion(*undefined*).
          1. Perform ? _m_.ModuleDeclarationInstantiation().
          1. Assert: All dependencies of _m_ have been transitively resolved and _m_ is ready for evaluation.
          1. Return ? _m_.ModuleEvaluation().