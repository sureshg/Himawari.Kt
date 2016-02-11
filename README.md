Himawari.Kt
-----------

Fetch live images of the Earth captured by the [Himawari-8](https://en.wikipedia.org/wiki/Himawari_8) satellite, then set them as desktop background on OSX. Written in [Kotlin](https://kotlinlang.org/)

Installation
------------

* From Binary

   [Mac OS X Download<sup>**</sup>](https://github.com/sureshg/Himawari.Kt/releases/latest)

   > After download and unzip, make sure to set the executable permission (`chmod +x himawari`)

* From Source

    ```bash
     $ git clone git@github.com:sureshg/Himawari.Kt.git
     $ cd Himawari.Kt
     $ ./gradlew clean makeExecutable
    ```
    > The binary would be located at `build/libs/himawari`

Usage
-----


```bash
$ ./himawari

```

Inspiration
-----------

Python Script for OSX: https://gist.github.com/willwhitney/e9e2c42885385c51843e

-------------
<sup>**</sup>Require [Java 8 or later](http://www.oracle.com/technetwork/java/javase/downloads/index.html)