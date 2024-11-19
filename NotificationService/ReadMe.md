# Message Queue Data WorkFLow
```plaintext
+------------------+                +------------------+                +---------------------+
|                  |                |                  |                |                     |
|  Message         |                |  Topic Exchange  |                |  Queue              |
|  Producer        | -- routeKey --> | reindeersExchange | --> notificationQueue --> Consumer
|                  |                |                  |                |                     |
+------------------+                +------------------+                +---------------------+

1. Producer sends a message with a routing key such as "notification.email" to "reindeersExchange"
2. The "reindeersExchange" routes the message to "notificationQueue" based on the routing key pattern "notification.#".
3. The Consumer listens on "notificationQueue" and processes messages, such as sending notifications to users.
