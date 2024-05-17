# Technologies Used in the Project

## Java
- **Reason for Use:** Extensive familiarity and expertise with it combined with time constraints and need for efficient progress, robust ecosystem and extensive libraries that offer a wealth of resources to expedite development tasks.
- **Considered Alternatives:** Python, due to its simplicity and suitability for AWS S3 interactions. However, lack of team expertise and potential longer development time led to the decision not to use it, especially given our time constraints.

## JUnit and Maven
- **Reason for Use:** JUnit facilitates robust and automated unit testing and smoothly integrates with Maven for streamlined project setup and dependency management, and extensive familiarity and experience with both.
- **Considered Alternatives:** None, as JUnit and Maven are widely adopted and well-suited for Java projects.

## Docker
- **Reason for Use:** Lightweight containerization, consistent deployment across environments, and minimal setup.
- **Considered Alternatives:** Other containerization solutions, but Docker's ease of use and popularity made it the preferred choice.

## Docker Swarm
- **Reason for Use:** Orchestration and management of containerized services for scalability and resilience, offers us full control over system's deployment, scaling, and maintenance.
- **Considered Alternatives:** Other orchestration tools such as Kubernetes, but Docker Swarm's compatibility with Docker containers and ability to give us finer-grained control made it the preferred choice.

## RabbitMQ
- **Reason for Use:** Reliable messaging system, support for various messaging patterns, integration with Java, and ease of use.
- **Considered Alternatives:** Apache Kafka, but RabbitMQ's simplicity and seamless integration with Java favored its selection.

## H264 Codec
- **Reason for Use:** Longevity, widespread device compatibility, extensive support, and ease of use.
- **Considered Alternatives:** Newer codecs like VP9, but their relative novelty and limited device support posed potential risks.

## HLS (HTTP Live Streaming)
- **Reason for Use:** Simplicity in implementation and widespread native support across devices and platforms.
- **Considered Alternatives:** DASH (Dynamic Adaptive Streaming over HTTP), but HLS aligned closely with current priorities and offered ease of setup and compatibility.

## FFmpeg
- **Reason for Use:** Comprehensive and robust multimedia processing tools for efficient video segmentation and transcoding, and widely used so reliable and stable.
- **Considered Alternatives:** GStreamer, HandBrake, Libav, but FFmpeg's versatility, robustness, and widespread acceptance made it the preferred choice.

## MongoDB
- **Reason for Use:** Simplicity and ease of management combined with project timeline, JSON-based data storage aligns with the type of data we're storing, and has a pre-made Docker image.
- **Considered Alternatives:** Apache Cassandra, Apache HBase, Google Cloud Bigtable, but they have steeper learning curves and more management complexity.
