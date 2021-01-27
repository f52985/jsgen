            1. If _d_ is *0*<sub>ℤ</sub>, throw a *RangeError* exception.
            1. If _n_ is *0*<sub>ℤ</sub>, return *0*<sub>ℤ</sub>.
            1. Let _r_ be the BigInt defined by the mathematical relation _r_ = _n_ - (_d_ × _q_) where _q_ is a BigInt that is negative only if _n_/_d_ is negative and positive only if _n_/_d_ is positive, and whose magnitude is as large as possible without exceeding the magnitude of the true mathematical quotient of _n_ and _d_.
            1. Return _r_.