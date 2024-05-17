# Distributed System Design Overview

## Use Cases for Distribution

- Efficient handling of large volumes of video transcoding tasks
- Improved scalability and availability
- Fault tolerance and resilience against component failures

## Reasons for Distribution

- Without distribution, processing multiple videos concurrently or dealing with exceptionally large video files could lead to significant delays, potentially rendering the service unusable.
- In a non-distributed system, if one component breaks, the whole system goes down. Distribution mitigates this risk by spreading the workload across multiple instances or nodes.
- Having multiple containers all working on transcoding reduces overall processing time and improves scalability and availability.

## Distributed System Challenges

1. **Fault Detection and Recovery**
   - Potential transient or permanent failure when trying to pull, transcode, or push
   - Potential full container failure
   - Prompt detection and requeuing of failed tasks without data loss or duplication
   
2. **Autoscaling**
   - Dynamically adjusting the number of video processing containers and nodes based on workload and resource utilization
   - Ensuring optimal performance and resource efficiency

3. **Redundancy**
   - Deploy RabbitMQ as a cluster, improving availibility and fault tolerance

## Approach to Address Challenges

### Fault Detection and Recovery
- Take advantage of the fact that Docker Swarm automatically restarts failed containers.
- Don't send RabbitMQ "ack" until task is complete, ensuring that it gets done.
- For transient errors, retry a few times before escalating.
- Escalatate non-recoverable errors immediately.

### Autoscaling
- Scale number of containers based on queue length.
- Scale number of nodes based on numbers of containers.

### Redundancy
- Deploy RabbitMQ as a cluster and handle the logistic issues that this presents, such as dynamically scaling the number of containers and coordinating which ports are used.

## Phase Two Enhancements

- Network latency optimization. As long as the system works in a normal amount of time, that is good enough for now. In stage 2 we could work on optimizing the system and making it work even faster.
- Deploy RabbitMQ as a cluster and handle the logistic issues that this presents, such as dynamically scaling the number of containers and coordinating which ports are used.
- Configure graceful shutdowns of Docker containers.
