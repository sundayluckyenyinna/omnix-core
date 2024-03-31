# Omnix-Core
## _The sweet cream on the bread for all omnix microservice_


>Design necessity and philosophy:  **_Anything that can be repeated, must be
>abstracted!_**

## Requirement & Usage
To use the omnix-core starter library, insert the following snippet to the pom.xml.
```html
  <dependency>
    <groupId>com.accionmfb.omnix</groupId>
    <artifactId>omnix-core</artifactId>
    <version>1.1.0</version>
  </dependency>
```

The above starter dependency is supported for **JDK 17 and above** and **Spring 3.1.2 and above**.

## Background
The omnix-core is a core library of the Omnix Middleware that must be incorporated into any simple Omnix micro-service. The library is an opinionated library that automates alot of manual tasks corresponding to cross-cutting concerns across all microservices.

Omnix-core design pattern follows the philosophy of the spring-boot paradigm which favours convention over configuration. This implies that the features and behaviour of the starter library is highly configurable at runtime. These configurations can be applied either by settings made in the application.yml/application.properties file or via annotations exposed by the starter library.

Furthermore, as it claims to be a starter library, once added to a project as a dependency, there is no further need for a manual configuration to include it in your project. Simply adding it to your project as a dependency declared in your maven build tool, it automatically registers in the spring-boot application context and all of its component beans are registered in the Dependency Injection container of the spring-boot factory!

## Features

-  Logging configuration.

-  Encryption configuration.

-  Global request/response/exception model format

-  Exception advice

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

| Option | Annotation Example                                                     | Description                                                                                   |
| ------- |------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------|
| EncryptionPolicy.REQUEST | @EncryptionPolicyAdvice(value = EncryptionPolicy.REQUEST)              | Declares that only the request payload of the API contract be encrypted from the client side. |
| EncryptionPolicy.RESPONSE | @EncryptionPolicyAdvice(value = EncryptionPolicy.RESPONSE)             | Declares that only the response payload of the API contract be encrypted from the server side |
| EncryptionPolicy.REQUEST_AND_RESPONSE | @EncryptionPolicyAdvice(value = EncryptionPolicy.REQUEST_AND_RESPONSE) | Declares that both the request and the response be encrypted |                                 |
| EncryptionPolicy.RELAX_ALL | @EncryptionPolicyAdvice(value = EncryptionPolicy.RELAX_ALL) | Declares that neither the request nor the response payload be encrypted.                      |                       

## Global Response/Exception Model Format
Definition of the response and exception format should be uniform to ensure the consistency of the server side response. The response and exception classes are outlined below:

- **ApiBaseResponse**
  This class is the base format for every response that will be sent out from the server side. This class defines the **responseCode**, **responseMessage** and the **errors** attributes without necessarily specifying the server response data. The JSON payload is as defined below:
```json
    {
       "responseCode": "07",
       "responseMessage": "Failed model",
       "errors": []
    }
```
- **OmnixApiResponse**
  This class extends the ApiBaseResponse and add the attribute to hold the response data. Thus, in the case a typical **OmnixResponse** server payload will include the **responseCode**, **responseMessage**, **errors** and the **responseData** from the server. The JSON definition is outlined below:

```json
    {
       "responseCode": "07",
       "responseMessage": "Failed model",
       "responseData": {},
       "errors": []
    }
```

- **OmnixApiException**
  This class follows the fluent design pattern. It is the standard exception that should be thrown against validation in the service or controller class. The OmnixAPiException thrown from anywhere in your project will be properly formatted by the library before reaching the client. The following shows a sample of how to construct and throw the exception to the client, when there is, for example, an insufficient balance on the customer's account for funds-transfer:

```java
    if(accountBalance < amountToDebit){
        throw OmnixApiException.newInstance()
            .withCode(ResponseCodes.INSUFFICIENT_BALANCE.getResponseCode())
            .withMessage("Insufficient balance")
            .withError("Sorry we cannot process your request at this time. You have insufficient balance to proceed")
            .withStatusCode(400);   // Bad Request
    }
```
The above is a very fluent design style for the OmnixApiException so that exception object can be built in the same time and line that they are thrown.The Exception advice that comes with the starter library will then format the response to the client as below:

```json lines
    400 (BAD REQUEST)
    {
       "responseCode": "05",
       "responseMessage": "Insufficient balance",
       "errors": ["Sorry we cannot process your request at this time. You have insufficient balance to proceed"]
    }
```

## Exception Advice
Common exceptions thrown on the Controller classes by Springboot are intercepted and properly formatted by the starter to the client application. The format returned to the client is a proper **OmnixApiException** with the corresponding http code captured from the exception object exposed by SpringBoot.
The following outlines some of the exceptions handled by the starter. This means that the developer does not have to handle this exceptions manually.

- OmnixApiException
- MethodArgumentNotValidException
- ConstraintViolationException
- ValidationException
- ResponseStatusException
- MissingServletRequestParameterException
- HttpMessageNotReadableException

## Synchronized parameterization/configuration
This is the most important feature of this starter library. It is the reason this library was created in the first place. The motivation behind this development is in the desire to constantly have a synchronization of configuration parameters defined by the application and within proximity. The traditional way of handling configurations is by defining these configurations in the **application.yml/application.properties** file. The following are the advantages of this approach:
- On application startup, even before the application context of the springboot bean factory are initialized, the Environment processor wires up all configurations defined in the file so that they can be available in the bean classes.
- It is easy to declare and reference configurations.

However, even with the advantages of the above traditional strategy, the disadvantage is not negligible. It is obvious that for every time there is a change or an update in the business requirement, the whole application will have to be redeployed!

Another option is to pull configurations at runtime from the database. A table in the database can be created for this purpose and all keys will be stored in one column while the corresponding value properties can be stored in another column of the same table. These two columns are sufficient to configure the properties of the application. Other columns such as description, created date, updated date etc can also be part of the table, but they are not necessary.
With the above approach, the problem of redeployment is eliminated. This is because if there is a new configuration requirement from the business, changes can be made right away in the database and the application will dynamically always read from the current value.

The disadvantage of the above database approach is the performance compromise relative to the approach of property files. The performance is not a big problem especially if the database server and the application server are on the same physical machine and location co-ordinate (but they might have different port co-ordinate).

The **omnix-core starter** library utilizes a mix of two approach in a hybrid fashion to take the advantages of the two approach while **_working around_** the disadvantages of the two approach.

**omnix-core** uses a **ConcurrentHashMap** to manage dynamically changing configurations that changes very often due to frequent changes of business requirements. At every time of the application lifecycle, the configurations in the HashMap are always in sync with the configurations in the database.

The working principle of local configuration cache registry is simple and straight forward in its approach.
- On application startup, **omnix-core** scans all enum classes annotated with the **@RequiredOmnixParam** annotation for all property keys that are needed by the application business logic.

- **omnix-core** read up all the values in the database corresponding to the keys read in the enum classes.
- When an update is made to add/update/delete a configuration, the update is made in the **Gateway** service application. This change will then trigger the Gateway service to notify all the services registered in the eureka server. This notification will then make all the registered services updates their ConcurrentHashMap of configuration registry.
- This way, the configurations in the ConcurrentHashMap will always be in sync with the database and only the needed configurations needed by the application is always in the memory of the application at a particular point in time.

.

**Usage**

To use the feature of the local cache registry, **omnix-core** exposes a bean that can be autowired in any other bean discoverable by the Spring application context or bean factory. The following example shows how this bean can be injected and used in a service class:

```java
// imports

@Slf4j
@Service
public class OmnixGuruDevelopmentClass{

    @Autowired
    private LocalParamCacheStorage cacheStorage;

    public ServerResponse<ResponseData> doSomeBusiness(RequestPayload requestPayload){
        String loanInterest = cacheStorage.getParamValue(LoanParam.LOAN_ASSESSMENT_INTEREST);
        
        // do some business with the loan interest.
    }

}

@RequiredOmnixParam
public enum LoanParam{

    LOAN_ASSESSMENT_INTEREST,
    LOAN_ASSESSMENT_MAX_TENURE
}
```

From the above, it is clear that:
- All configuration keys are stored in the enum class. **omnix-core** will automatically inject all the corresponding values into these keys from the database. Also, these values **_always in sync with changes of these configurations in the database_**.

- The getParamValue(Object key) method is called on the LocalParamCacheStorage interface to get the **_latest configuration value_**.

- The LocalParamCacheStorage always read from the internal RAM of the application server to gain extreme performance.

The above approach has the following advantages:

- Configurations are always read from the proximity of the application server RAM. This implies a very high increase in performance compared to the option of traditional database lookup.
- Application configuration registry are always in sync with the database configuration table.
- There is no fear for application crash as configuration is not stored in an external cache storage such as Redis. Redis cache might run out of its limited memory or even shut down in its server and this would mean a potential crash of applications that depends on it for configuration values.


## Configurable Method fallback mechanism
The developer might find himself/herself not using this feature at all, but it is available for use where cases demands its use. This feature leverages on the Spring boot Aspect Oriented Programming (AOP) to provide fallback mechanism for methods. This enables very clean coding structure such that obvious service methods return types are separated from fallbacks values or desired return types.

The following outlines an example of the use of this fallback mechanism:

```java

// imports

@Service
@RequiredArgsConstructor
@FallbackAdvice(value = OmnixGuruDevelopmentServiceFallback.class)
public class OmnixGuruDevelopmentService{

    private final ServiceClass serviceClass;

    @FallbackHandler(methodName = "doSomeBusinessFallback" onValue = { "null" })
    public Object doSomeBusiness(RequestPayload requestPayload){
        
        // some pre business logic
        return serviceClass.getSomething();
    }
  
    // other service methods
}



public class OmnixGuruDevelopmentServiceFallback{

    public Object doSomeBusinessFallback(@FallbackParam("requestPayload") RequestPayload requestPayload){
        return "Some fallback return value";
    }

    // other fallback methods.
}
```
.

From the above, the following is observed:
- OmnixGuruDevelopmentService is a service that has some business methods **doSomeBusiness** that has the potential to return **null** value. The method is annotated with the **FallbackHandler** annotation. Notice the **onValue** parameter of the annotation that dictates when the fallback method must be called by virtue of the value returned by the **doSomeBusiness** method. The fallback method called **doSomeBusinessFallback** is defined in the fallback class OmnixGuruDevelopmentServiceFallback.
- OmnixGuruDevelopmentService is annotated with the **@FallbackAdvice** annotation stating the OmnixGuruDevelopmentServiceFallback fallback class.
- OmnixGuruDevelopmentServiceFallback holds the definition and implementations of the fallback method(s).
- @FallbackParam annotation is used in the OmnixGuruDevelopmentServiceFallback class to bind the arguments of the calling class to the fallback methods.


.

# Accion MFB Software - Private License

This software is the property of Accion MFB and is provided under the following license.

## License Agreement

**By using this software, you agree to the following terms and conditions:**

1. **Grant of License:**
  - Accion MFB grants you a limited, non-exclusive, non-transferable license to use, copy, modify, and distribute this software internally within your organization.

2. **Restrictions:**
  - You may not sublicense, sell, lease, or otherwise transfer the software to any third party.
  - Reverse engineering, decompiling, or disassembling the software is strictly prohibited.

3. **Ownership:**
  - Accion MFB retains all ownership and intellectual property rights to the software.

4. **No Warranty:**
  - This software is provided "as is," without warranty of any kind, express or implied. Accion MFB makes no representations or warranties regarding the use or performance of this software.

5. **Limitation of Liability:**
  - In no event shall Accion MFB be liable for any damages, including but not limited to direct, indirect, special, incidental, or consequential damages arising out of the use or inability to use the software.

6. **Compliance with Laws:**
  - You agree to comply with all applicable laws and regulations in your use of the software.

7. **Modification:**
  - You may modify the software for internal use only. Any modifications must be clearly marked as such and not misrepresented as the original software.

8. **Termination:**
  - This license is effective until terminated. Accion MFB may terminate this license at any time if you fail to comply with its terms. Upon termination, you must cease using the software and destroy all copies.

## Contact Information

For questions regarding this license, please contact Accion MFB at [support@example.com](mailto:sundayluckyenyinnadeveloper@gmail.com).

© 2023 Accion MFB. All rights reserved.
