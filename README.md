# Maintain trust details frontend

This service is responsible for updating the information held about the trust in a trust registration.

The service allows a user to update their residency, registered on another EEA register and other information in a standard maintenance joureny.

The service allows a user to answer questions to make the trust taxable on a non-taxable to taxable migration.

To run locally using the micro-service provided by the service manager:

`sm2 --start TRUSTS_ALL`

If you want to run your local copy, then stop the frontend ran by the service manager and run your local code by using the following (port number is 9838 but is defaulted to that in build.sbt).

`sbt run`

### To Test
To run the local tests run the below script

`./run_all_tests.sh`

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
