This is a Kotlin Multiplatform project targeting Android, iOS.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
    - `commonMain` is for code that’s common for all targets.
    - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
      For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
      `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* `/shared` is for the code that will be shared between all targets in the project.
  The most important subfolder is `commonMain`. If preferred, you can add code to the platform-specific folders here too.

* `/sharedModule` is similar with `/shared` but this folder will be share all the modules for different platform.

## Installation

To install this page, please use Android Studio or Intellij IDE to let the gradle to install the necessary packages and build it. And you could use xcode if you'll build it on iOS.

## Tech Stack

**Kotlin:** any version of Kotlin, but currently I use version 2

**Library:** I use jetpack compose to help my development

**Gradle:** currently, I update the gradle to latest version for further implementation

## Demo

Currently, this shell just support for android only and for iOS is going to be developed as soon as possible. To demo the shell, just change the URL to your hosted Web based app to *MainActivity.kt* inside *webView.loadUrl()* function. Then, run the *composeApp*.


## Features

- This project mainly focused on building web view for multiplatform. Why? Because currently, I've develop some mobile app using React Native. So, I need the shell for my app.


## To-Do List

- ~Setup Base Project~
- ~Setup Base Config and Packages~
- ~Setup Android~
- ~Setup Bridges~
- ~Add Camera Bridge~
- ~Add File Bridge~
- Add iOS Support
- Cleaning Up the Code

