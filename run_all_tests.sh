#!/bin/bash

sbt clean scalastyle compile coverage test it:test coverageOff coverageReport dependencyUpdates
