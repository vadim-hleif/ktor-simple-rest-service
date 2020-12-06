## Ktor-simple-rest-service

[![Unit tests](https://github.com/vadim-hleif/ktor-simple-rest-service/workflows/Unit%20tests/badge.svg?branch=master&event=push)](https://github.com/vadim-hleif/ktor-simple-rest-service/actions?query=workflow%3A%22Unit+tests%22)

## What is it
A simple example of rest api service, without db and authentication.
Data will be generated at the runtime.

Domain is flat viewing schedule.
It allows reserving slots from tenant side, and rejected or approve reservation from landlord side.
Once rejected slot can't be used anymore.

Viewing slots will be generated for 7 upcoming days, the first slot will be available in 24h after application start.
Time window of each slot is 20 minutes.

Each action (reservation, cancellation, approving, rejection) will be logged in the console.
## REST API

Get all flats (with slots inside and references to landlords)
```shell script
curl --location --request GET 'http://127.0.0.1:8080/flats'
```

Get all tenants
```shell script
curl --location --request GET 'http://127.0.0.1:8080/tenants'
```

Reserve the slot by the tenant
```shell script
curl --location --request PATCH 'http://localhost:8080/flats/214/slots/1' \
--header 'Content-Type: application/json' \
--data-raw '{"tenantId": 639}'
```

Approve reservation
```shell script
curl --location --request PATCH 'http://localhost:8080/flats/214/slots/1' \
--header 'Content-Type: application/json' \
--data-raw '{"state": "APPROVED"}'
```

Reject reservation
```shell script
curl --location --request PATCH 'http://localhost:8080/flats/214/slots/1' \
--header 'Content-Type: application/json' \
--data-raw '{"state": "REJECTED"}'
```

Release the reservation
```shell script
curl --location --request DELETE 'http://localhost:8080/flats/214/slots/1'
```