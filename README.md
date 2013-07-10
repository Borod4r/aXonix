aXonix
======

Remake of the famous Xonix game for Android and Desktop (using LibGDX)

Project Layout
--------------

* Core project ("/sources/Main"):

  Contains all the code of this application, minus the so called starter classes.
  All other projects link to this project.

* Android project ("/sources/Android"):

  Contains the starter class and other necessary files to run this application on Android.
  The assets/ folder stores the assets of this application for all platforms.

* Desktop project ("/sources/Desktop"):

  Contains the starter class to run this application on the desktop.
  Links to the Android project's assets/ folder as well as the core project.

Project Setup
-------------

* Eclipse

  https://code.google.com/p/libgdx/wiki/ProjectSetupNew

* Intellij IDEA

  https://code.google.com/p/libgdx/wiki/IntelliJIDEALibgdx
