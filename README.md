# This program was created as a semester project of the NSS

-- -- --

## (CTU - SIT summer semester 2023)

-- -- --

### Author: Dmitry Rastvorov

-- -- --

### Java version: 20

-- -- --

### Contents:

#### 1. [UML](#uml)
#### 2. [Postman](#postman)
#### 3. [Documentation](#doc)
#### 4. [Selected appropriate technology and language](#satal)
#### 5. [Project description](#desc)
#### 6. [Use of a common DB](#db)
#### 7. [Comments in source code](#com)
#### 8. [Use of cache](#cache)
#### 9. [Use of messaging principle](#msg)
#### 10. [Security and Authorisation implementation](#sec)
#### 11. [Use of Interceptors](#inter)
#### 12. [Use of the technologies](#tech)
#### 13. [Deployment to a production server, e.g. Heroku](#deploy)
#### 14. [Suitable architecture](#arch)
#### 15. [Initialization procedure](#init)
#### 16. [Used design patterns](#pat)
#### 17. [Use Cases](#use)
#### 18. [Cloud services](#cloud)

-- -- --

### <a name="uml"></a>UML diagram ([Powered by IntelliJ Ultimate](https://www.jetbrains.com/help/idea/class-diagram.html)):

![](https://github.com/UnknownPug/web-chat/assets/73190129/7cfa204d-b916-488b-9e6a-1c6034ccbd7c)


-- -- --

### <a name="postman"></a>Postman:

- The link to the Postman requests for usage can be
  found [here](https://documenter.getpostman.com/view/22903223/2s9Y5Ww2t6)

-- -- --

### <a name="doc"></a>Documentation

#### Web Chat Application Description and Motivation:

In the realm of online communication, efficient and timely interactions can be challenging to achieve.
Our upcoming project focuses on addressing this issue within web-based chat systems.
With a primary focus on User and Administrator
roles, our project aims to enhance communication and streamline interactions in a web chat environment.

**"The Connected Conversations"** project strives to revolutionize web chat interactions.
By providing users with a platform to engage seamlessly and administrators with tools to manage
and oversee conversations, we intend to bridge the gap in digital communication.

#### Strategic Plan (TO-BE):

- Implementation of a web chat application catering to both Users and Administrators.
  Users can engage in conversations, seek assistance, and provide feedback,
  while Administrators can monitor discussions, address issues, and gather insights.


- Deployment of a web-based administrative interface that empowers Administrators to respond promptly to user inquiries,
  facilitates productive discussions, and gather valuable conversational data.


- Integration of a moderation system, enabling Administrators to review and manage chat content, ensuring it aligns with
  community guidelines and standards.


- Objectives for measurable improvements in communication:

        • Onboarding of 5% of website visitors as active Users.
        • Engagement of 80% of Administrators in utilizing the chat management tools.

#### Business Benefits (AS IS):

- Currently, users may encounter challenges in finding a suitable platform for web-based conversations, leading to
  missed opportunities for connections and solutions.


- Users might struggle with direct communication channels, often lacking a seamless and user-friendly means of
  addressing queries.


- Administrators may experience difficulties in managing and monitoring chat interactions, potentially resulting in
  delayed responses and unresolved issues.


- There exists a gap in fostering a unified digital community where Users actively participate in discussions for
  positive changes.

#### 5F Analysis:

##### 1) Competition:

- Limited competitors in the web chat optimization space, offering an advantageous position to promote our application
  on a global scale.
  Supplier Strength:


- The project's dependency primarily lies on Internet Service Providers, mitigating risks associated with traditional
  suppliers.
  Customer Strength:


- Our target audience encompasses both individual citizens and organizations seeking efficient online interactions.


- Citizens will utilize the platform to engage in conversations, while organizations can leverage it for customer
  support and engagement.

##### 2) Threat of Substitute Products:

- The potential for direct substitutes is minimal due to the uniqueness of our web chat application and the current
  absence of similar offerings.

##### 3) Potential for New Entrants:

- The potential for new entrants to enter this market remains feasible, potentially influencing the dynamics of our
  market sector.

#### PEST(E) Analysis:

##### 1) Political:

- The application aligns with neutral and non-political principles, strictly serving as a platform to foster productive
  conversations and address communal concerns.

##### 2) Economic:

- The project operates within the stable IT industry standards, poised to meet the demands of web-based product
  competition.

##### 3) Social:

- The application prioritizes user privacy and data security, ensuring user information is not exploited for
  unauthorized purposes.

##### 4) Technological:

- The application boasts stable functionality, leveraging cutting-edge technologies to ensure seamless user
  experiences.

##### 5) Environmental:

- The project's digital nature minimizes environmental impact, as it operates without significant natural resource
  consumption and energy usage.

#### Users:

The web chat system will cater to the following user types:

    ● User
    ● Administrator

**User:**

Registered Users will enjoy the ability to engage in meaningful conversations, seek assistance, provide feedback, and
participate in discussions.

**Administrator:**

Administrators will possess tools to oversee conversations, respond promptly to queries, manage interactions, and uphold
community guidelines, fostering a positive online environment.

![sd](https://github.com/UnknownPug/web-chat/assets/73190129/de55d0f3-bbd6-4dd6-98d4-61f5d42ccc77)


-- -- --

#### <a name="satal"></a>Selected appropriate technology and language

    ● Server-side: Java Spring Boot
    ● HTTP client for testing API: Postman
    ● Database: PostgresSQL
    ● Cache: Hazelcast
    ● Messaging: Apache Kafka
    ● Security: Basic Auth
    ● Interceptors: Spring Boot Interceptors
    ● Technologies: REST
    ● Deployment: Amazon RDS
    ● Architecture: MVC
    ● Used IDE: Intellij IDEA

-- -- --

#### <a name="desc"></a>Project description: what is done and where the functionality is located

- The implemented functionality of the project is located [here](https://github.com/UnknownPug/web-chat/tree/main/src).

-- -- --

#### <a name="db"></a>Use of a common DB

- For the server part, we used PostgresSQL.

-- -- --

#### <a name="com"></a>Comments in source code

- The code is commented in English, and the documentation can be found in the source code, or you can read javadoc.

-- -- -

#### <a name="cache"></a>Use of cache

- For the caching application was used Hazelcast.

-- -- --

#### <a name="msg"></a>Use of messaging principle

- For the messaging principle application was used Apache Kafka.

-- -- --

#### <a name="sec"></a>Security and Authorisation implementation

- For the security and authorization implementation application was used Basic Auth.

-- -- --

#### <a name="inter"></a>Use of Interceptors

- For the interceptors' application was used Spring Boot Interceptors for logging and authentication.

-- -- --

#### <a name="tech"></a>Use of the technologies

- The project was used REST technology.

-- -- --

#### <a name="deploy"></a>Deployment to a production server, e.g. Heroku

- The project was deployed to Amazon RDS, but a free tier version has been expired.

-- -- --

#### <a name="arch"></a>Suitable architecture

- The project is using MVC architecture.

-- -- --

#### <a name="init"></a>Initialization procedure

1) In order to download the project, you need to open the terminal
2) Select the location where you would like to place the project
3) Write ``git clone <SSH link>`` and click ``Enter``.
4) After installation is complete, you need to download [Kafka](https://kafka.apache.org/downloads).
5) After you download Kafka, you need to unzip it and open the terminal in the Kafka folder.
6) In the terminal, you need to write ``bin/zookeeper-server-start.sh config/zookeeper.properties`` and click ``Enter``.
   With this command, you will start Zookeeper.
7) Next step is to open another terminal
   and write ``bin/kafka-server-start.sh config/server.properties`` and click ``Enter``.
   With this command, you will start Kafka broker service.
8) After that, you need to open one more terminal and write
   ``bin/kafka-topics.sh --create --topic <topic name> --bootstrap-server localhost:9092`` and click ``Enter``.
   With this command, you will create a topic, where you will se the messages, that you will send to the server from the
   client.
9) The next step is to open your IDE and open the project.
10) Then you must run pom.xml clean and package and then run the project by running `Application.java` method.
11) You are ready to go! :D

-- -- --

### <a name="pat"></a>Used design patterns

    ● Dependency Injection (DI)
    ● Service Layer Pattern
    ● Repository Pattern
    ● Caching Pattern
    ● Factory Method Pattern
    ● Exception Handling
    ● Template Method Pattern
    ● DTO (Data Transfer Object)
    ● Facade Pattern
    ● Strategy Pattern
    ● Transactional Pattern
    ● Builder Pattern
    ● State Pattern
    And more...

-- -- --

### <a name="use"></a>Use Cases

![](https://github.com/UnknownPug/web-chat/assets/73190129/8c25296b-beb1-46b5-81ba-149c93775710)

-- -- --

### <a name="cloud"></a>Cloud services

- Amazon AWS cloud service was used, but a free tier version has been expired.

-- -- --

### Thank you for your attention! 😄
