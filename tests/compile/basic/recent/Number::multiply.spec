            1. If _x_ is *NaN* or _y_ is *NaN*, return *NaN*.
            1. If _x_ is *+∞*<sub>𝔽</sub> or _x_ is *-∞*<sub>𝔽</sub>, then
              1. If _y_ is *+0*<sub>𝔽</sub> or _y_ is *-0*<sub>𝔽</sub>, return *NaN*.
              1. If _y_ > *+0*<sub>𝔽</sub>, return _x_.
              1. Return -_x_.
            1. If _y_ is *+∞*<sub>𝔽</sub> or _y_ is *-∞*<sub>𝔽</sub>, then
              1. If _x_ is *+0*<sub>𝔽</sub> or _x_ is *-0*<sub>𝔽</sub>, return *NaN*.
              1. If _x_ > *+0*<sub>𝔽</sub>, return _y_.
              1. Return -_y_.
            1. Return 𝔽(ℝ(_x_) × ℝ(_y_)).