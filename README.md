# tonlib-java

This is a JVM wrapper for TonLib that can be used with Java/Scala/Kotlin/etc.

TonLib is a C++ client-side library for interacting with TON.

The basic necessary functionality is safely and securely implemented in TonLib.

TonLib checks the Merkle-proofs of data received from the liteserver, so the library can be used with public liteservers.

Java interacts with TonLib via JNI, `Client.java` and generated typed messages classes declared in `TonApi.java`.


# Example

The repository contains the `TonTestJava.java` example of use.

Compile and Run:

```bash
cd src

javac drinkless/org/ton/TonTestJava.java

java -cp . -Djava.library.path=$(pwd) drinkless/org/ton/TonTestJava
```

# Artifacts

The repository contains already built libraries for Windows, MacOS and Ubuntu.

You can take the latest library and TonApi.java from [TON autobuilds](https://github.com/newton-blockchain/ton/actions?query=branch%3Amaster+is%3Acompleted).

* tonlib-java/ folder for MacOS and Ubuntu

* native-lib.dll for Windows (You can take TonApi.java from MacOS or Ununtu autobuild)

> Note: there are no autobuilds for the Apple M1 processor yet.

# Build tonlib and generate TonApi.java

If necessary, you can manually rebuild the C++ libraries and re-generate TonApi.java as described below.

## Set Java variables

Java must be installed.

Check that the `JAVA_HOME` variable is set.

Set JNI variables:

```bash
export JAVA_AWT_LIBRARY=NotNeeded
export JAVA_JVM_LIBRARY=NotNeeded
export JAVA_INCLUDE_PATH=${JAVA_HOME}/include
export JAVA_AWT_INCLUDE_PATH=${JAVA_HOME}/include

# export JAVA_INCLUDE_PATH2=${JAVA_HOME}/include/<platform>
# for MacOS:
export JAVA_INCLUDE_PATH2=${JAVA_HOME}/include/darwin
# for Linux: 
export JAVA_INCLUDE_PATH2=${JAVA_HOME}/include/linux
```

## Install TON Dependencies

Install the newest versions of make, cmake (version 3.22.1 or later), OpenSSL (version 1.1.1 or later, including C header files), and g++ or clang (or another C++14-compatible compiler as appropriate for your operating system).

## Generate and build

```bash
git clone --recurse-submodules -j8 https://github.com/newton-blockchain/ton

cd ton

git checkout wallets

cd example/android/

mkdir build

cd build

cmake -DTON_ONLY_TONLIB=ON ..

cmake --build . --target prepare_cross_compiling

cmake --build . --target native-lib
```

After `prepare_cross_compiling` your will get generated `TonApi.java` in `ton/example/android/src/drinkless/org/ton/TonApi.java`

After `native-lib` your will get `libnative-lib.so` (for Linux) or `libnative-lib.dylib` (for MacOS) in build directory.

Copy `TonApi.java` and `libnative-lib` to your project.
