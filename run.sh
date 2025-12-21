#!/bin/bash

mvn clean compile
mvn exec:java -Dexec.mainClass="com.coursework.server.Main"