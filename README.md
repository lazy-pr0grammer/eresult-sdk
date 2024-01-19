# Education Board Result SDK

### For educational purpose!

The **EResult** SDK is designed to facilitate the retrieval of exam results from
the [Education Board Bangladesh](https://eboardresults.com/bn/ebr.app/home/) website. This document
provides an overview of the SDK's classes, methods, and usage.

## Table of Contents

1. [Installation](#installation)
2. [Getting Started](#getting-started)
    - [Initialization](#initialization)
3. [Requesting Captcha](#requesting-captcha)
4. [Requesting Exam Results](#requesting-exam-results)
5. [More usage](#more)

## Installation

To use the EResult SDK in your Android project, include the following dependency in your app's
build.gradle file:

Add the JitPack repository to your project's root `settings.gradle` file:

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://jitpack.io") } //Add this line
    }
}
```

## Step 2: Add the Dependency

Add the eresult-sdk dependency to your app module's `build.gradle` file:

 ```gradle
dependencies {
    implementation("com.github.lazy-pr0grammer:eresult-sdk:1.0") // For gradle.ktx
    implementation 'com.github.lazy-pr0grammer:eresult-sdk:1.0' // For gradle.groovy
}
```

## Getting Started

### Initialization

Before using the EResult SDK, make sure to initialize it with the appropriate dependencies. Add the
following to your application class or entry point.
To create an instance of the **EResult** class, use the provided Builder:
For Java:

```java
EResult result = new EResult.Builder()
        .setYear("2023")
        .setResultType(ResultType.INDIVIDUAL)
        .setRegistrationId("123456")
        .setStudentRollNumber("7890")
        .setBoardType(BoardType.DHAKA)
        .setExamType(ExamType.SSC)
        .build();
```

For Kotlin:

```kotlin
val result = EResult.Builder()
    .setYear("2023")
    .setResultType(ResultType.INDIVIDUAL)
    .setRegistrationId("123456")
    .setStudentRollNumber("7890")
    .setBoardType(BoardType.DHAKA)
    .setExamType(ExamType.SSC)
    .build()
```

## Requesting Captcha

Use the following method to request a captcha image asynchronously:
For Java:

```java
result.requestCaptcha(new ResultCallback<Bitmap>(){
@Override
public void onResponse(Bitmap result){
        // Handle the captcha image response
        }

@Override
public void onFailure(String result){
        // Handle the failure
        }
        });
```

For Kotlin:

```kotlin
result.requestCaptcha(object : ResultCallback<Bitmap?> {
    override fun onResponse(result: Bitmap?) {
        // Handle the captcha image response
    }
    override fun onFailure(result: String?) {
        // Handle the failure
    }
})
```

## Requesting Exam Results

After obtaining the captcha, use the following method to request exam results asynchronously:
For Java:

```java
result.requestResult("captchaCode",new ResultCallback<String>(){
@Override
public void onResponse(String result){
        // Handle the exam result response
        }

@Override
public void onFailure(String result){
        // Handle the failure
        }
        });
```

For Kotlin:

```kotlin
result.requestResult("captchaCode", object : ResultCallback<String> {
    override fun onResponse(result: String?) {
        // Handle the result response
    }
    override fun onFailure(result: String?) {
        // Handle the failure
    }
})
```

## More

The SDK contains 5 kinds of result type

```kotlin
BOARD //Incomplete
CENTER //Provides center based result
DISTRICT //Provides district based result
INDIVIDUAL //Provides single result (Requires Roll and Reg No)
INSTITUTION //Provides institution based result (Requires EIIN)
```

Also contains 3 exam type

```kotlin
JSC, SSC, HSC
```

It also has all boards. You can call it like this

```java
BoardType type = BoardType.CHITTAGONG;
```

```kotlin
val type = BoardType.CHITTAGONG
```

---

For more information and examples, refer to the source code and inline documentation in the EResult
SDK.
