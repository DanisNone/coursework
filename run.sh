#!/bin/bash

cd server
mvn clean compile exec:java -Dexec.mainClass="com.coursework.server.Main"