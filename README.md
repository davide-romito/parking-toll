# Getting Started
* The application runs on Java 8 (at least).

## How to build the app
The project can be built using `mvn clean install`.
To build it without performing a test phase can be use `mvn clean install -DskipTests`. 

## How to run the app
### From IntelliJ
* Right-click on `application/src/main/java/com/library/parkingtoll/ParkingTollApplication`, click `Run ParkingTollApplication`

### From the command line 
The app is developed using Spring Boot and can be launched as a jar with an embedded Tomcat:
```
# First build the project, then from the parking-toll folder:
$ mvn spring-boot:run
# or
$ java -jar target/parking-toll-0.0.1-SNAPSHOT.jar
```

## Documentation
The API documentation is being published on Swagger. There are two main endpoints:
* `http://localhost:8080/v2/api-docs` to get the OpenAPI specification file
* `http://localhost:8080/swagger-ui.html` for the Swagger documentation

## How to test the app
### Create Parking
* Request structure
```
{
    "parkingName": PARKING_NAME,
    "pricingPolicy":PRICING_POLICY,
    "price": {
        "hourRate" : HOUR_RATE,
        "fixRate" : FIX_RATE
    },
    "parkingSlots" : [
        {
            "type" : CAR_TYPE,
            "availableSpot" : NUMBER_OF_SPOTS
        }, ... 
        ]
 }
```
* `PARKING_NAME` : optional parking name
* `PRICING_POLICY` : mandatory. Can be FIX_PLUS_HOURLY or HOURLY 
* `HOUR_RATE` : mandatory. Float value 
* `FIX_RATE` : optional. Float value 
* `CAR_TYPE` : mandatory. Can be STANDARD, ELECTRIC_POWERED_20_KW, ELECTRIC_POWERED_50_KW
* `NUMBER_OF_SPOTS` : mandatory. Number of spot
* Curl example
```
curl -d '{
            "parkingName": "parking",
            "pricingPolicy":"FIX_PLUS_HOURLY",
            "price": {
                "hourRate" : 5.0,
                "fixRate" : 10.0
            },
            "parkingSlots" : [
                {
                    "type" : "STANDARD",
                    "availableSpot" : 5
                }, 
                {
                    "type" : "ELECTRIC_POWERED_20_KW",
                    "availableSpot" : 5
                }, 
                {
                    "type" : "ELECTRIC_POWERED_50_KW",
                    "availableSpot" : 5
                }
                ]
         }' -H "Content-Type: application/json" -X POST http://localhost:8080/createParking
```

### Park a car
* Request structure
```
{
    "type" : CAR_TYPE
}
* `CAR_TYPE` : mandatory. Can be STANDARD, ELECTRIC_POWERED_20_KW, ELECTRIC_POWERED_50_KW
```
* URL `/parking/PARKING_NAME/parkCar` where PARKING_NAME is the name or the id of the parking 
* Curl example
```
curl -d '{
            "type" : "STANDARD"
         }' -H "Content-Type: application/json" -X POST http://localhost:8080/parking/parking/parkCar

```
### Take a car
* URL `/parking/PARKING_NAME/takeCar/PARKING_SLOT_ID` where PARKING_NAME is the name or the id of the parking 
and PARKING_SLOT_ID is the id of the slot where the car is parked 
* Curl example
```
curl http://localhost:8080/parking/parking/takeCar/1
```
