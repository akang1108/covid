# Covid

* Get data from John Hopkins
* Retrieve and graph client side

## loader

* Gets data from John Hopkins dataset on Github
* Creates json data in ui/src/public/

## ui

* Vuejs app using chart.js

## Run locally

```bash
### Build and run loader
$ cd loader
$ ./gradlew clean run

### Build and run ui
$ cd ../ui
$ npm install
$ npm run dev

### View ui
$ open http://localhost:8080/covid
```

