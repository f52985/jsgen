        1. Let _label_ be the StringValue of |LabelIdentifier|.
        1. Let _newLabelSet_ be a copy of _labelSet_ with _label_ appended.
        1. Return ContainsUndefinedContinueTarget of |LabelledItem| with arguments _iterationSet_ and _newLabelSet_.