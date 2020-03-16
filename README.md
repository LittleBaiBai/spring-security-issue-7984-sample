# Spring Security Issue 7984 Sample

Run all the tests:
```
./gradlew test
```

When a request is made to endpoint A (`/test/a`), it will call endpoint B (`/test/b`) with `WebClient`. Currently the `callEndpointB` test passes, but `callEndpointA` test fails.
