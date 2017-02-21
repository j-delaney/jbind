JBind
=====
A partial Karabiner replacement that works with Sierra.
It allows you to select a hyper key (like Capslock) and use that as a modifier.
For example, the default `config.json` file sets it so that if the user is holding Capslock then IJKL behave as arrow keys.

**IMPORTANT:** Right now, I am developing this for myself.
I do plan to eventually clean up this code and making it more user-friendly.
I am also by no means an expert in the JVM/Kotlin/Maven so expect a lot of pretty bad code.

Setting Up
----------
These are the instructions I use for my personal setup.

First thing you'll need to do is download [Karabiner Elements](https://github.com/tekezo/Karabiner-Elements) and install it.
Then you should remap `caps_lock` to `F18`.

Build the `jar` file with `mvn package`.

Run it! `java -jar target/jbind-1.0.0-jar-with-dependencies.jar`.