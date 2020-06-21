import * as lib from './lib';
console.log('lib', lib);

console.log("Valid amount?", lib.validate_Amount({ currency: 'EUR', value: 1.0 }));

console.log("map", lib.schema);
console.log("calc", lib.check_something(1, 2));
console.log("fn", lib.check_something.toString());

// After generating .d based on clojure analyzer, this should work:
//console.log("testrec", new lib.testrec(1, 2, 3));
