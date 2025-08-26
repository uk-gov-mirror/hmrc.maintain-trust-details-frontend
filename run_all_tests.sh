#!/bin/bash

sbt clean compile coverage test IntegrationTest/test coverageOff coverageReport dependencyUpdates
