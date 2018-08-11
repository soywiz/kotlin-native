### Q: How do I run my program?

Define top level function `fun main(args: Array<String>)`, please ensure it's not
in a package. Also compiler switch `-entry` could be use to make any function taking
`Array<String>` and returning `Unit` be an entry point.


### Q: What is Kotlin/Native memory management model?

Kotlin/Native provides automated memory management scheme, similar to what Java or Swift provides.
Current implementation includes automated reference counter with cycle collector to collect cyclical
garbage.


### Q: How do I create shared library?

Use `-produce dynamic` compiler switch, or `konanArtifacts { dynamic('foo') {} }` in Gradle.
It will produce platform-specific shared object (.so on Linux, .dylib on macOS and .dll on Windows targets) and
C language header, allowing to use all public APIs available in your Kotlin/Native program from C/C++ code.
See `samples/python_extension` as an example of using such shared object to provide a bridge between Python and
Kotlin/Native.


### Q: How do I create static library or an object file?

Use `-produce static` compiler switch, or `konanArtifacts { static('foo') {} }` in Gradle.
It will produce platform-specific static object (.a library format) and C language header, allowing to
use all public APIs available in your Kotlin/Native program from C/C++ code.


### Q: How do I run Kotlin/Native behind corporate proxy?

As Kotlin/Native need to download platform specific toolchain, you need to specify
`-Dhttp.proxyHost=xxx -Dhttp.proxyPort=xxx` as compiler's or `gradlew` arguments,
or set it via `JAVA_OPTS` environment variable.


### Q: How do I specify custom Objective-C prefix/name for my Kotlin framework?

Use `-module_name` compiler option or matching Gradle DSL statement, i.e.
```
framework("MyCustomFramework") {
    extraOpts '-module_name', 'TheName'
}
```


### Q: Why do I see `InvalidMutabilityException`?

It likely happens, because you are trying to mutate a frozen object. Object could transfer to the
frozen state either explicitly, as objects reachable from objects on which `konan.worker.freeze` is called,
or implicitly (i.e. reachable from `enum` or global singleton object - see next question).


### Q: How do I make a singleton object mutable?

Currently, singleton objects are immutable (i.e. frozen after creation), and it's generally considered
a good practise to have global state immutable. If for some reasons you need mutable state inside such an
object, use `@konan.ThreadLocal` annotation on the object. Also `konan.worker.AtomicReference` class could be
used to store different pointers to frozen objects in a frozen object and atomically update those.

### Q: How can I compile my project against Kotlin/Native master?

We release dev builds frequently, usually at least once a week. You can check the [list of available versions](https://bintray.com/jetbrains/kotlin-native-dependencies/kotlin-native-gradle-plugin). But in the case we recently fixed an issue and you want to check before a release is done, you can do:

<details>
    
<summary>For the CLI, you can compile using gradle as stated in the README (and if you get errors, you can try to do a <code>./gradlew clean</code>):</summary>

```
./gradlew dependencies:update
./gradlew dist distPlatformLibs
```

You can then set the `KONAN_HOME` env variable to the generated `dist` folder in the git repository.

</details>

<details>
<summary>For Gradle, you can use <a href="https://docs.gradle.org/current/userguide/composite_builds.html">Gradle composite builds</a> like this:</summary>

```
# Set with the path of your kotlin-native clone
export KONAN_REPO=$PWD/../kotlin-native

# Run this once since it is costly, you can remove the `clean` task if not big changes were made from the last time you did this
pushd $KONAN_REPO && git pull && ./gradlew clean dependencies:update dist distPlatformLibs && popd

# In your project, you set have to the konan.home property, and include as composite the shared and gradle-plugin builds
./gradlew check -Pkonan.home=$KONAN_REPO/dist --include-build $KONAN_REPO/shared --include-build $KONAN_REPO/tools/kotlin-native-gradle-plugin
```

</details>

### Q: How to debug the compiler with the gradle plugin?

First of all, you will need to tell gradle to wait for a debugger to attach. You can do it by making this environment
variable available:

```
export GRADLE_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
```

<details>
<summary>Then you can run your gradle task from source (explained in the how to compile question), for example:</summary>

```
export KONAN_REPO=$PWD/../kotlin-native
./gradlew check -Pkonan.home=$KONAN_REPO/dist --include-build $KONAN_REPO/shared --include-build $KONAN_REPO/tools/kotlin-native-gradle-plugin
```

This will output something like this:
```
Listening for transport dt_socket at address: 5005
```
</details>

<details>
<summary>Now that gradle is waiting for a debugger to connect, let's open the `kotlin-native` project inside IntelliJ IDEA and debug a Remote session:</summary>

You have to create a new Run/Debug **Remote** configuration by clicking on the dropdown <kbd>Edit configurations...</kbd>, then
pressing the <kbd>+</kbd> button, and then selecting the <kbd>Remote</kbd> item from the list.
This will create a new Remote configuration with the 5005 port already selected.

Then press the OK button and debug the configuration. You can now put breakpoints in the code.
Remember that if you change things, you will have to recompile. Depending on the thing changed,
you can execute `./gradlew dist` or `./gradlew distPlatformLibs` or both or call additional tasks. 
</details>
