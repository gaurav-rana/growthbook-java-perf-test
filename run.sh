# Check if FEATURES_ENDPOINT is not set
if [ -z "$FEATURES_ENDPOINT" ]; then
    echo "FEATURES_ENDPOINT is not set in the environment variable. Please enter the FEATURES_ENDPOINT:"
    read FEATURES_ENDPOINT
    export FEATURES_ENDPOINT
fi

mvn clean install

sdkVersion=$(grep -A 2 'artifactId>growthbook-sdk-java<' pom.xml | grep '<version>' | sed -e 's/<version>\(.*\)<\/version>/\1/')
htmlFileName=$(echo "$sdkVersion" | tr '.' '_' | tr -d '[:space:]')
echo "SDK Version: $sdkVersion"

mode=$1
if [ "$mode" = "cpu" ]; then
    echo "Profiling with CPU"
    echo "Reports would be available here: reports/${htmlFileName}-cpu-10million-boolean-feature.html"
    java -agentpath:/Users/gaurav.rana/Downloads/async_profiler/async-profiler-3.0-macos/lib/libasyncProfiler.dylib=start,event=cpu,file=reports/${htmlFileName}-cpu-10million-boolean-feature.html -jar target/growthbook-java-perf-test-1.0-SNAPSHOT.jar
elif [ "$mode" = "alloc" ]; then
    echo "Profiling with Allocation"
    echo "Reports would be available here: reports/${htmlFileName}-alloc-10million-boolean-feature.html"
    java -agentpath:/Users/gaurav.rana/Downloads/async_profiler/async-profiler-3.0-macos/lib/libasyncProfiler.dylib=start,event=alloc,file=reports/${htmlFileName}-alloc-10million-boolean-feature.html -jar target/growthbook-java-perf-test-1.0-SNAPSHOT.jar
else
    echo "Please provide a valid mode: cpu, alloc"
    exit 1
fi
#

