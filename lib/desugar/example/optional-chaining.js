// Input
var obj = {f () {console.log("Original Function")}};
obj.f.call = function() {console.log("Overwritten Function");};
obj.f?.();
