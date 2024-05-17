# Video transcoding and segmentation

**Team Members:** [Ephraim Crystal](mailto:ecrysta1@mail.yu.edu), [Lawrence Snow](mailto:lsnow@mail.yu.edu), [Yonatan Reiter](mailto:yreiter@mail.yu.edu)

## Project Plan

- [Scope and Use Cases](scope.md)
- [Distributed System Challenges](challenges.md)
- [Workflow Diagrams (BPMN)](workflow.md)
- [Software Architecture Diagrams (C4)](architecture.md)
- [Tools & Technologies](technologies.md)

## Overview

_Provide a short introduction, summary and functional overview of what your project group is trying to achieve and implement. Give the context within the greater system and application._

Our project serves as a middleman between Groups 1 and 3. Group 1 will provide us with a distributed storage service that allows users to upload videos; from our perspective, this is essentially an S3 bucket. Group 3 segments each video into multiple parts based on the number of available nodes, making it easier for us to process them.

Our project retrieves these videos and applies a number of transformations to them. We convert each video into a number of different resolutions and bitrates, allowing clients to choose between various options depending on their available bandwidth. We do all of this in a highly distributed manner, concurrently handling the processing of many video files and auto-scaling as necessary. Finally, we log status updates to a database, which will be needed by Group 3 in order for them to implement failure handling.

We then upload the transcoded videos to the database. Group 3 is responsible for handling workflow management and client-facing issues using the infrastructure we built, so they will handle sending the video to the client.
