# Video transcoding and segmentation

**Team Members:** [Ephraim Crystal](mailto:ecrysta1@mail.yu.edu), [Lawrence Snow](mailto:lsnow@mail.yu.edu), [Yonatan Reiter](mailto:yreiter@mail.yu.edu)

## Links

- [System Overview](Project%20Description%20and%20Presentation/System%20Overview.pdf)
- [Slideshow](Project%20Description%20and%20Presentation/Video%20Streaming%20Capstone%20Presentation.pptx)
- [Scope and Use Cases](Project%20Description%20and%20Presentation/scope.md)
- [Distributed System Challenges](Project%20Description%20and%20Presentation/challenges.md)
- [Workflow Diagrams (BPMN)](Project%20Description%20and%20Presentation/workflow.md)
- [Software Architecture Diagrams (C4)](Project%20Description%20and%20Presentation/architecture.md)
- [Tools & Technologies](Project%20Description%20and%20Presentation/technologies.md)

## Overview

When watching a video on YouTube, the video quality is dynamically adjusted based on the viewer's internet bandwidth to ensure the smoothest possible viewing experience. We set out to create a system that replicates this functionality. Videos have to be prepared to be streamed in such a manner. This preparation entails transcoding videos into multiple resolutions and bitrates to provide options for our clients depending on their bandwidth. Our project focuses on this preparation aspect.

Clients send us requests to transcode videos stored in an object store. We retrieve the relevant videos, transcode them into various resolutions and bitrates, and upload the final results back to the object store. We do all of this in a highly distributed manner, concurrently handling the processing of many video files and auto-scaling as necessary. Throughout this process, we log status updates to a database and publish them to a message bus.
