# Orders

## Problem 
Create a real-time system that emulates the fulfillment of delivery orders for a kitchen.  
* The kitchen should receive 2 delivery orders per second.  
* The kitchen should instantly cook the order upon receiving it  
* Place the order on the best-availible shelf to await pick up by a courier  
 
Receive the order, dispatch a courier to pick up and deliver that specific order  
The courier should arrive randomly between 2-6 seconds later. The courier should instantly pick up the order upon arrival.The courier should instantly deliver it.  

## Orders  
Orders must be parse from the file and ingested into your system 2 ordes per second.  
```
{
"id": "0ff534a7-a7c4-48ad-b6ec-7632e36af950",
"name": "Cheese Pizza",
"temp": "hot", // Preferred shelf storage temperature
"shelfLife": 300, // Shelf wait max duration (seconds)
"decayRate": 0.45 // Value deterioration modifier
}
```
## Shelves  
The kitchen pick-up area has multiple shelves to hold cooked at different temperatures.  
Each order should be placed on a shelf that matches the order's temperature. If the shelf is full the an order can be placed on the overflow shelf.  
If the overflow shelf is full, a random order should be discarded as waste from the overflow shelf.  

Hot shelf     :10  
Cold shelf    :10   
Frozen shelf  :10  
Generic shelf :15  

## Shelf Life  
Orders have an inherent value that will deteriorate over time, based on the order's shelfLife and decayRate fields. Orders that have reached a value of zero are considered wasted.  
sh = shelfLife - orderAge - decayRate * orderAge * shelfDecayModifier / shelfLife.  
shelfDecayModifier 1 for simple temp and 2 for the overflow shelf.  


## Calculate usage  
The requests/ orders per second will be 2 per second so:   
1 request per second = 2.5 million requests per month  
2 request per second = 5 million requests per month  

## Analysis  
As the problems consists in simulate a real time system delivering orders and the traffic will be heavy after I analyzed and  
I decided to use a Event driven architecture so all the orders will be deliver asynchronously.  
 

## Architecture and Patterns  

I decided to use an event based architecture because this architecture fits well the requirements.
I am using the library called RX-JAVA to help me to implement this pattern using non-blocking asynchronous programming  also using the observable pattern.  

## Concurrency Model  
Functional programming is the process of building software by composing pure functions, avoiding shared state, mutable data, and side-effects  
with this components we can avoid another problems when we are using blocking programming and share state in concurrency such data race or race contitions.  
Also I decided to use it because althogth I am not an expert in concurrency I know that dealing when share state in concurrency is difficult when we need to scale and trace bugs.  


The key part I analized to user rx-java was that it could help to get the following features out of the box:  
* Responsive: The systems should respond in a timely manner.  
* Message Driven: Systems should use async message-passing between components to ensure loose coupling.  
* Elastic: Systems should stay responsive under high load.  
* Resilient: Systems should stay responsive when some components fail.  


## Application Components:  
The application consists in 4 components:  
Kitchen  --> Subject  
Hot Shelf --> FlowableProcessor  
Cold Shelf --> FlowableProcessor  
Frozen Shelf --> FlowableProcessor  
Generic Shelf --> FlowableProcessor  

The subject and FlowableProcessor both are components provided by rx-java and both functionality is almost the same receive and send events the
only difference is flowableProcessor can support BackPressure.  
The flow is in that way: the Kitchen receive the initial orders from the json file, then the kitchen sends the orders by temperature to the especific shelf
the shelf's size for each element is 10 and the generic's size is 15. When the shelf is full sends the order to the generic shelf and when the generic shelf is full discard the order
from the generic shelf.  

As each elements receive and emit orders there are 4 couriers.  

Diagram  
