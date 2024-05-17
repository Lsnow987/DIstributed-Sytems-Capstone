## Overview

System architecture overview (see [here](workflow.md#client-requests-processing-of-video) for basic use case; for how autoscaling works, see [here](workflow.md#autoscaling-workflow)):

![SimplifiedC4](https://github.com/S24-Capstone-Distributed/General-4020/assets/76976043/91c3dddd-9ecb-4c52-9ea0-cb72e1807114)

One level deeper:

![VideoProcessing_FINAL_Overview](https://github.com/S24-Capstone-Distributed/General-4020/assets/76976043/117fa891-fd8f-4903-a767-969340f52165)

## Components

See [here](workflow.md#error-handling-functional-container-error) and [here](workflow.md#error-handling-container-failure) for how the general two types of failure are dealt with.

Video retrieval/pulling:

![VideoProcessing_RetrievalLatest](https://github.com/S24-Capstone-Distributed/General-4020/assets/76976043/90392f87-7020-45fb-b865-e65f62ee0dff)

Video processing:

![VideoProcessing_ProcessingLatest](https://github.com/S24-Capstone-Distributed/General-4020/assets/76976043/40b1231c-a2b4-4f47-8cc4-1ecd5440cfc3)

Video sending/pushing:

![VideoProcessing_SendingLatest](https://github.com/S24-Capstone-Distributed/General-4020/assets/76976043/0a7ea0c6-5fa2-46ac-adf9-e5d0621d12a8)
