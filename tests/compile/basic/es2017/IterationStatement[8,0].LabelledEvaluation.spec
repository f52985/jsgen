          1. Let _keyResult_ be the result of performing ? ForIn/OfHeadEvaluation(« », |AssignmentExpression|, ~iterate~).
          1. Return ? ForIn/OfBodyEvaluation(|LeftHandSideExpression|, |Statement|, _keyResult_, ~iterate~, ~assignment~, _labelSet_).