## Table of Contents

1. [Client Requests Processing of Video](#feature-client-requests-processing-of-video)
2. [Autoscaling Workflow](#feature-autoscaling-workflow)
3. [Error Handling - Functional Container Error](#feature-error-handling-functional-container-error)
4. [Error Handling - Container Failure](#feature-error-handling-container-failure)


## Relevant Events and Descriptions

### 1. TaskReceived
**Description:** Event triggered when a new video transcoding task is received from the message bus / queue.

### 2. TaskDetailsRetrieved
**Description:** Event triggered after retrieving task details from the database using the provided task ID.

### 3. VideoFilePulled
**Description:** Event triggered after successfully pulling the necessary video file from the object store based on the task details.

### 4. VideoTranscoded
**Description:** Event triggered after the video file has been transcoded to desired resolutions and bitrates.

### 5. TranscodedVideoUploaded
**Description:** Event triggered after successfully uploading the transcoded video file back to the storage service.

### 6. WorkerNodeScaledUp
**Description:** Event triggered when a new worker node is spun up to handle increased workload.

### 7. WorkerNodeScaledDown
**Description:** Event triggered when an underutilized worker node is scheduled for shutdown after finishing its current tasks.

### 8. ContainerErrorRetry
**Description:** Event triggered when an error is encountered during the processing of a video transcoding task, and the system retries the operation.

### 9. ContainerErrorEscalation
**Description:** Event triggered when an error is encountered during the processing of a video transcoding task, and the issue is escalated to system administrators for manual intervention.

### 10. ContainerFailureDetected
**Description:** Event triggered upon detecting a failure of the container within the processing pod, initiating recovery procedures.

### 11. HeartbeatReceived
**Description:** Event triggered upon receiving a heartbeat signal from a container or pod within the system.

### 12. GossipTableMerged
**Description:** Event triggered after merging received gossip tables to ensure consistency and update records accordingly.

### 13. ContainerScaledUp
**Description:** Event triggered when additional containers are launched to distribute workload and alleviate resource constraints during workload processing.

### 14. ContainerScaledDown
**Description:** Event triggered when containers are shut down after finishing their current jobs to optimize resource utilization during workload processing.

### 15. ResourceUtilizationThresholdReached
**Description:** Event triggered when CPU or RAM utilization reaches predefined thresholds during workload processing, prompting scaling actions.

### 16. WorkloadProcessingStarted
**Description:** Event triggered when a container starts processing a video transcoding task, signaling its activity.

### 17. WorkloadProcessingFinished
**Description:** Event triggered when a container finishes processing a video transcoding task, signaling its inactivity.


## Feature: Client Requests Processing of Video

Scenario: Processing of Video Task

Given a client request received over the message bus / queue

When the message is taken off of the message bus / queue

Then a TaskReceived event is logged to the database

And the necessary video file is pulled from the object store based on the task details

Then a VideoFilePulled event is logged to the database

And the video file is transcoded to desired resolutions and bitrates

Then a VideoTranscoded event is logged to the database

And the transcoded video file is uploaded back to the storage service

Then a TranscodedVideoUploaded event is logged to the database


## Feature: Autoscaling Workflow

Scenario: Dynamic Scaling Based on Queue Size

Given the video processing system is running

When the service continuously monitors the queue length

Then it compares the queue length to a pre-defined range

And if the queue length is within the range

Then the system configuration remains the same


And if the queue length is too high

And the ratio of containers to nodes is above a pre-defined threshold

Then start up a new node

And a WorkerNodeScaledUp event is logged to the database

And start up a new container on that node

And a ContainerScaledUp event is logged to the database

And if the ratio of containers to nodes is below the threshold

Then start up a new container

And a ContainerScaledUp event is logged to the database


And if the queue length is too low

And the ratio of containers to nodes is above a pre-defined threshold

Then stop sending work to a container

And shut down the container when it finishes its current work

And a ContainerScaledDown event is logged to the database

And if the ratio of containers to nodes is below the threshold

Then stop sending work to a specific container

And shut down the container when it finishes its current work

And a ContainerScaledDown event is logged to the database

And copy over any containers running on that node to another node

And shut down the node

And a WorkerNodeScaledDown event is logged to the database


And the system continues to monitor queue length

And dynamically adjusts nodes and containers accordingly


## Feature: Error Handling - Functional Container Error

Scenario: Retry and Escalation for Functional Container Error

Given the system is actively processing video transcoding tasks

And the functional container encounters an error during its operation

When the error is in pulling or pushing the video file

Then the system checks the retry count against a predefined threshold

And if the retry count is below the threshold, the operation is retried with exponential backoff

But if the retry count exceeds the threshold, the error is escalated

Then a ContainerErrorEscalation event is logged to the database

And system administrators investigate and resolve the issue

And once resolved, the system updates the task status and resumes processing


## Feature: Error Handling - Container Failure

Scenario: Detection and Recovery of Container Failure

Given the system is actively processing video transcoding tasks

And a container within the processing pod fails

When the failure is detected by Docker Swarm

Then a new container is automatically spawned

And a ContainerFailureDetected event is logged to the database

And failed tasks are requeued for processing

And progress updates and status changes are logged for monitoring purposes
