// Input
var obj = {f () {console.log("Original Function")}};
obj.f.call = function() {console.log("Overwritten Function");};
obj.f?.();

/* Output
"use strict";

var _obj$f;

var obj = {
  f: function f() {
    console.log("Original Function");
  }
};

obj.f.call = function () {
  console.log("Overwritten Function");
};

(_obj$f = obj.f) === null || _obj$f === void 0 ? void 0 : _obj$f.call(obj);
*/
