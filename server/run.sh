#!/bin/bash

mvn clean compile exec:java -Dexec.mainClass="com.coursework.server.Main" -Dexec.args="193.108.113.136 8080"