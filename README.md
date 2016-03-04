# Untangled • [TodoMVC](http://todomvc.com)

A client-only standard todomvc written with Untangled.

## Running it:

Start figwheel (the JVM options tell figwheel which builds to run):

```
JVM_OPTS="-Ddev -Dtest" lein run -m clojure.main script/figwheel.clj
```

which should start auto-building the cljs source and show a browser REPL.

Navigate to: [http://localhost:2345/dev.html](http://localhost:2345/dev.html)

Changes to the source should re-render without a browser reload. 

## Running the tests

The figwheel build above will start the client test build. Simply open
(any number of) browsers on 
[http://localhost:2345/test.html](http://localhost:2345/test.html)

## Credit

Created by [NAVIS](http://www.thenavisway.com)
