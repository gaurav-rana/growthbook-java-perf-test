Download Async Profiler from the following link:
[Async Profiler](https://github.com/async-profiler/async-profiler/releases)

Build the project with the following command:
```bash
mvn clean install
```
Run the project with the following command:
```bash
export FEATURES_ENDPOINT="https://cdn.growthbook.io/....."
sh run.sh <mode>
```
Where `<mode>` can be `cpu` or `alloc`.
```

It'll create the reports in `reports/` directory. Open it in a browser to see the flamegraph.