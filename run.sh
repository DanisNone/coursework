#!/bin/bash

javac -Xlint Main.java
java -Dfile.encoding=UTF-8 -cp ".:database/sqlite-jdbc-3.51.1.0.jar" Main
