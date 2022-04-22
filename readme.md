This program was made specifically to test skills when joining a company. Provides two options where backtest is written using imperative programming and reactiveTest is written using reactive programming. If you are doing the test yourself, I strongly do not recommend watching or copying the solution in order for your own solution to develop in your case.

In case you want to try and test the solution yourself or even improve it, feel free to copy this repository.

The difference is visible both in writing code and in the speed of test execution.

For tests inside docker there is created application-docker.yml which permit you to replace automatically "http://localhost:3001" to "simulado" which is internal adress to docker image. 

Also for test passing, the best way to start is running command below.
Depending on which programm you want to test, you need to chenge name of folder in the command and also you might be change the URI in test.js when you want to launch it.

```
docker-compose up -d --build backtest/reactiveTest simulado influxdb grafana 
```

There is a Dockerfile created which can be used for easy deploy app on docker.

# Backend dev technical test
We want to offer a new feature to our customers showing similar products to the one they are currently seeing. To do this we agreed with our front-end applications to create a new REST API operation that will provide them the product detail of the similar products for a given one. [Here](./similarProducts.yaml) is the contract we agreed.

We already have an endpoint that provides the product Ids similar for a given one. We also have another endpoint that returns the product detail by product Id. [Here](./existingApis.yaml) is the documentation of the existing APIs.

**Create a Spring boot application that exposes the agreed REST API on port 5000.**

![Diagram](./assets/diagram.jpg "Diagram")

Note that _Test_ and _Mocks_ components are given, you must only implement _yourApp_.

## Testing and Self-evaluation
You can run the same test we will put through your application. You just need to have docker installed.

First of all, you may need to enable file sharing for the `shared` folder on your docker dashboard -> settings -> resources -> file sharing.

Then you can start the mocks and other needed infrastructure with the following command.
```
docker-compose up -d simulado influxdb grafana
```
Check that mocks are working with a sample request to [http://localhost:3001/product/1/similarids](http://localhost:3001/product/1/similarids).

To execute the test run:
```
docker-compose run --rm k6 run scripts/test.js
```
Browse [http://localhost:3000/d/Le2Ku9NMk/k6-performance-test](http://localhost:3000/d/Le2Ku9NMk/k6-performance-test) to view the results.

## Evaluation
The following topics will be considered:
- Code clarity and maintainability
- Performance
- Resilence
