Download Async Profiler from the following link:
[Async Profiler](https://github.com/async-profiler/async-profiler/releases)

Build the project with the following command:
```bash
mvn clean install
```
Run the project with the following command:
```bash
export FEATURES_ENDPOINT="https://cdn.growthbook.io/....."
java -agentpath:/path/to/asyncprofiler/lib/libasyncProfiler.dylib=start,event=cpu,file=profiler.html -jar target/growthbook-java-perf-test-1.0-SNAPSHOT.jar
```

It'll create a file called `profiler.html` in the current directory. Open it in a browser to see the flamegraph.