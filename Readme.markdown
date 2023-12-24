# Omnix-Core
## _The sweet cream on the bread for all omnix microservice_

[![N|Solid](https://cldup.com/dTxpPi9lDf.thumb.png)](https://nodesource.com/products/nsolid)

[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)
.

>Design necessity and philosophy:  **_Anything that can be repeated, must be
>abstracted!_**

.
## Background
The omnix-core is a core library of the Omnix Middleware that must be incorporated into any simple Omnix micro-service. The library is an opinionated library that automates alot of manual tasks corresponding to cross-cutting concerns across all microservices.

Omnix-core design pattern follows the philosophy of the spring-boot paradigm which favours convention over configuration. This implies that the features and behaviour of the starter library is highly configurable at runtime. This configurations can be applied either by settings made in the application.yml/application.properties file or via annotations exposed by the starter library.

Furthermore, as it claims to be a starter library, once added to a project as a dependency, there is no further need for a manual configuration to include it in your project. Simply adding it to your project as a dependency declared in your maven build tool, it automatically registers in the spring-boot application context and all of its component beans are registered in the Dependency Injection container of the spring-boot factory!

## Features

-  Logging configuration.

-  Encryption configuration.

-  Global request/response/exception model format

-  Exception advice

-  Instrumentation

-  Synchronized parameterization/configuration

-  Configurable Method fallback mechanism

.
## Logging Configuration
Immediately the starter library is added to your project, default configurations are applied for logging every request and response of your exposed APIs. This is done so that you do not have to perform the mundane operation of request/response logs and make you just focus on the business logic of your API.

The style of the request/response logs can be configured in the **application.yml** or **application.properties** file of choice in the following way:

Property: **logger.config.style**

Possible values for the above configuration includes:

- **default**: This logs request/response using the system default style of logging.
-  **pretty_print**: This logs request/response such that the Request Body Json and the Response Body Json are logged in a pretty format.

If there is a mistake in your statement of the supported logging style
above, the default configuration is applied.

Further to the configuration above is the use of **annotations** to add more control to the API request/response logging strategy. The annotation provides declarative idom to control the strategy of the logging. Examples of when this control will be needed is in the case of ignoring large logs output that might quickly fill up the log file of the application. Thus, there might cases when only the **request** or the **response** is required to be logged. The following annotation describes a sufficiency for this requirement:
- **@HttpLoggingAdvice**
  This annotation provide sufficient advice to the controller method. It is a method only annotation and complements any logging configuration set up in the property files of the application. It is to be noted that while the properties files declares the styling of logging strategy, the annotation declares final declarations on the final behaviour of th e annotation.
  The above annotation takes a parameter that dictates the direction of the logging. The table below summarizes the options for the direction of logging.
  .

| Option | Annotation example | Description |
|--------|--------------------|-------------|
| LogPolicy.REQUEST | @HttpLoggingAdvice(direction=LogPolicy.REQUEST) | This specifies that only the request of the API of the calling client be logged. |
| LogPolicy.RESPONSE | @HttpLoggingAdvice(direction=LogPolicy.RESPONSE) | This specifies that only the response of the API of the server be logged. |
| LogPolicy.REQUEST_AND_RESPONSE | @HttpLoggingAdvice(direction=LogPolicy.REQUEST_AND_RESPONSE) (Default)| This specifies that both the request and the response of the API of the calling client and the server respectively be logged. |
| LogPolicy.RELAX_ALL | @HttpLoggingAdvice(direction=LogPolicy.RELAX_ALL) | This specifies that neither the request nor the response of the API of the calling client and the server respectively be logged. |

## Encryption Configuration
Within the Omnix security policy includes the necessity of API contract encryption. All requests and responses must be encrypted end-to-end to prevent the man in the middle attack. This process of applying decryption for every request in EVERY controller classes and then applying decryption in same EVERY controller is mundane. Following the philosophy of this starter, encryption is abstracted in this library. The good news is that the developer does not have to do any extra configuration to use it. Upon addition of this starter to your project, every request will automatically be decrypted BEFORE hitting your controller and every response will automatically be encrypted AFTER leaving your controller but before reaching the client requesting for your specific API resource.
In the case of development, there might be several configurations you might want to apply. You might even want to turn the feature of encryption off totally while testing and developing your API. The following are the configurations that can be applied to control the global behaviour of the encryption feature:

| Property | Default | Description |
|----------|---------|-------------|
| omnix.encryption.enable-encryption | true | Declares that encryption should be applied globally for every request and response. |
|omnix.encryption.algorithm | AES | Declares that the AES algorithm be used for the purpose of the encryption |
| omnix.encryption.aes-encryption-key | ********** | Declares the encryption that is used by the encryption algorithm |

Fine-grained and high level control might be desired by the developer for some use case scenerio. For example, the developer might want to expose an API such that the request end of the API contract be encrypted and vice -versa. For these special case scenerios, annotations are used to decorate and advice every controller class or specific controller methods. Below outlines the full details of the **@EncryptionPolicyAdvice**. The annotation can be applicable on the method level and advices the particular controller mapping on the strategy of the encryption policy.

| Option | Annotation Example | Description |
| ------- | ------------------ | ----------- |
| EncryptionPolicy.REQUEST | @EncryptionPolicyAdvice(value = EncryptionPolicy.REQUEST) | Declares that only the request payload of the API contract be encrypted from the client side. |
| EncryptionPolicy.RESPONSE | @EncryptionPolicyAdvice(value = EncryptionPolicy.RESPONSE) | Declares that only the response payload of the API contract be encrypted from the server side |
| EncryptionPolicy.REQUEST_AND_RESPONSE | @EncryptionPolicyAdvice(value = EncryptionPolicy.RELAX_ALL) | Declares that neither the request nor the response payload be encrypted. |

## Global Response/Exception Model Format
Definition of the response and exception format should be uniform to ensure the consistency of the server side response. The response and exception classes are outlined below:

- **ApiBaseResponse**
  This class is the base format for every response that will be sent out from the server side. This class defines the **responseCode**, **responseMessage** and the **errors** attributes without necessarily specifying the server response data. The JSON payload is as defined below:
```
    {
       "responseCode": "07",
       "responseMessage": "Failed model",
       "errors": []
    }
```
- **OmnixApiResponse**
  This class extends the ApiBaseResponse and add the attribute to hold the response data. Thus, in the case a typical **OmnixResponse** server payload will include the **responseCode**, **responseMessage**, **errors** and the **responseData** from the server. The JSON definition is outlined below:

```
    {
       "responseCode": "07",
       "responseMessage": "Failed model",
       "responseData": {},
       "errors": []
    }
```

- **OmnixApiException**
  This class follows the fluent design pattern. It is the standard exception that should be thrown against validation in the service or controller class. The OmnixAPiException thrown from anywhere in your project will be properly formatted by the library before reaching the client. The following shows a sample of how to construct and through the exception to the client, when there is, for example, an insufficient balance on the customer's account for funds-transfer:

```
    if(accountBalance < amountToDebit){
        throw OmnixApiException.newInstance()
            .withCode(ResponseCodes.INSUFFICIENT_BALANCE.getResponseCode())
            .withMessage("Insufficient balance")
            .withError("Sorry we cannot process your request at this time. You have insufficient balance to proceed")
            .withStatusCode(400);   // Bad Request
     }
```
The above is a very fluent design style for the OmnixApiException so that exception object can be built in the same time and line that they are thrown.The Exception advice that comes with the starter library will then format the response to the client as below:

```
    400 (BAD REQUEST)
    {
       "responseCode": "05",
       "responseMessage": "Insufficient balance",
       "errors": ["Sorry we cannot process your request at this time. You have insufficient balance to proceed"]
    }
```