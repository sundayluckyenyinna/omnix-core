                                  OMNIX - CORE

Design necessity and philosophy: **Anything that can be repeated, must be
abstracted!**

The omnix-core is a core library of the Omnix Middleware that must be
incorporated into any simple Omnix micro-service. The library is an
opinionated library that automates alot of manual tasks corresponding to
cross-cutting concerns across all microservices.

Omnix-core design pattern follows the philosophy of the spring-boot
paradigm which favours convention over configuration. This implies that
the features and behaviour of the starter library is highly configurable
at runtime. This configurations can be applied either by settings made
in the application.yml/application.properties file or via annotations
exposed by the starter library.

Furthermore, as it claims to be a starter library, once added to a
project as a dependency, there is no further need for a manual
configuration to include it in your project. Simply adding it to your
project as a dependency declared in your maven build tool, it
automatically registers in the spring-boot application context and all
of its component beans are registered in the Dependency Injection
container of the spring-boot factory!

The major features of the omnix-core library includes:

1.  **Logging configuration.**

2.  **Encryption configuration.**

3.  **Global request/response/exception response format**

4.  **Exception advice**

5.  **Instrumentation**

6.  **Synchronized parameterization/configuration**

7.  **Configurable Method fallback mechanism**

8.  **Logging**

Immediately the starter library is added to your project, default
configurations are applied for logging every request and response of
your exposed APIs. This is done so that you do not have to perform the
mundane operation of request/response logs and make you just focus on
the business logic of your API.

The style of the request/response logs can be configured in the
application.yml/application/properties file on choice in the following
way:

Property: logger.config.style

Possible values for the above configuration includes:

a.  default: This logs request/response using the system default style
    of logging.
b.  pretty_print: This logs request/response such that the Request Body
    Json and the Response Body Json are logged in a pretty format.

If there is a mistake in your statement of the supported logging style
above, the default configuration is applied.

2.  Encryption Configuration Within the Omnix security policy includes
    the necessity of API contract encryption. All requests and responses
    must be encrypted end-to-end to prevent the man in the middle
    attack. This process of applying decryption for every request in
    EVERY controller classes and then applying decryption in same EVERY
    controller is mundane. Following the philosophy of this starter,
    encryption is abstracted in this library. The good news is that the
    developer does not have to do any extra configuration to use it.
    Upon addition of this starter to your project, every request will
    automatically be decrypted BEFORE hitting your controller and every
    response will automatically be encrypted AFTER leaving your
    controller but before reaching the client requesting for your
    specific API resource.

In the case of development, there might be several configurations you
might want to apply. You might even want to turn the feature of
encryption off totally while testing and developing your API. The
following are the configurations that can be applied to control the
beaviour of the encryption feature:

property: omnix.encryption.enable-encryption = true/false (turn on/turn
off encryption) property: omnix.encryption.algorith = AES (choose the
encryption algorithm to use. Current Supported algorithm is AES)
property: omnix.encryption.aes-encryption-key = xxxxxxx (specifies the
encryption key for the encryption algorithm)

The above configurations might not be enough to work with the encryption
feature. For example, the following cases might come up during your
development and release of your API design.

Case 1: Only the request from the client should be encrypted while the
response should not necessarily be encrypted. Case 2: Only the response
from the API should be encrypted from the API while the client request
does not need to be necessarily encrypted. Case 3: A specific API
request and response should not necessarily be encrypted but all other
API resource should follow the strict encryption policy.

For the above cases, it is clear that the configurations is primarily
for specific endpoints/API resource. Thus, annotations on each endpoint
is the best way to seprate this concern that is not cross-cutting. The
following annotations addresses the above special cases:

A. @EncryptionPolicyAdvice (value =
EncryptionPolicy.REQUEST_AND_RESPONSE)

The above annotation applied on a controller advices the controller on
the encryption policy of annotation to use for the API resource
associated to it. The annotation has an 'EncryptionPolicy' key that
specifies the exact policy for the advice. The key must have values in
the 'com.accionmfb.omnix.core.commons' package. The values and their
effect of the Encryption Policy enums include:

a.  EncryptionPolicy.REQUEST (specifies that ONLY client request be
    encrypted and that the controller should decrypt it before use).
b.  EncryptionPolicy.RESPONSE (specifies that ONLY server response be
    encrypted).
c.  EncryptionPolicy.REQUEST_AND_RESPONSE (specifies that both the
    request and response be encrypted/decrypted)
d.  EncryptionPolicy.RELAX_ALL (specifies that the encryption should be
    relaxed totally for both the request and response)

```{=html}
<!-- -->
```
3.  Global request/response/exception response format For the sake of
    uniformity across all microservices, the request/response and
    exception format between clients and server must be the same. This
    uniformity is ensured by the starter library that defines several
    classes for this case:

```{=html}
<!-- -->
```
a.  ApiBaseResponse: This class is a pojo with the following JSON
    definition

```{=html}
<!-- -->
```
    {
       "responseCode": "07",
           "responseMessage": "Failed model",
           "errors": []
    }

    The above can be used to respond to the client without a response data.

b.  OmnixApiResponse: This class is a pojo with the following JSON
    definition

```{=html}
<!-- -->
```
    {
       "responseCode": "07",
           "responseMessage": "Failed model",
       "responseData": {},
           "errors": []
    }

    The above can be used to respond to the client with a response data.

c.  OmnixApiException: This class follows the fluent design pattern. It
    is the standard exception that should be thrown against validation
    in the service or controller class. The OmnixAPiException thrown
    from anywhere in your project will be properly formatted by the
    library before reaching the client. The following shows a sample of
    how to construct and through the exception to the client, when there
    is, for example, an insufficient balance on the customer's account
    for funds-transfer:

```{=html}
<!-- -->
```
     if(accountBalance < amountToDebit){
        throw OmnixApiException.newInstance()
            .withCode(ResponseCodes.INSUFFICIENT_BALANCE.getResponseCode())
            .withMessage("Insufficient balance")
            .withError("Sorry we cannot process your request at this time. You have insufficient balance to proceed")
            .withStatusCode(400);   // Bad Request
     }

    The above is a very fluent design style for the OmnixApiException so that exception object can be built in the same time and line that they are
        thrown.

    The Exception advice that comes with the starter library will then format the response to the client as below:

    400 (BAD REQUEST)
    {
       "responseCode": "05",
           "responseMessage": "Insufficient balance",
           "errors": ["Sorry we cannot process your request at this time. You have insufficient balance to proceed"]
    }

4.  Exception Advice The starter library handles three kinds of
    exceptions that might be thrown between the client request and the
    server response. These classes of exception includes:

```{=html}
<!-- -->
```
a.  MethodArguementNotValidException: The starter library handles the
    generic and default spring-boot ugly exception logs when a wrong
    method arguement is passed. The format that will be appreciated by
    the client response model. The reformatted form will now be of
    something below:

```{=html}
<!-- -->
```
    {
       "responseCode": "07",
           "responseMessage": "Failed model",
           "errors": ["Failed model"],
           "responseData": null
    }

b.  ConstraintViolationException: The starter library handles the
    generic and default spring-boot ugly exception logs to a format that
    will be appreciated by the client response model. The
    ConstraintViolationException is invoked when there is violation of
    the request model or contract that ws meant to be satisfied by the
    client. For example, when a null value is passed for a non-nullable
    field in the request model. The reformatted form will now be of
    something below:

```{=html}
<!-- -->
```
    {
       "responseCode": "07",
           "responseMessage": "mobileNumber cannot be null",
           "errors": ["mobileNumber cannot be null"],
           "responseData": null
    }

c.  MissingServletParameterException: The starter library handles the
    generic and default spring-boot ugly exception logs. The exception
    is invoked when there is a missing request parameter or passed
    variable in the API resource request from the client.

d.  ResponseStatusException: The starter library handles properly the
    exception that is thrown when there is a status exception.

e.  OmnixApiException: The standard exception format for omnix is
    handled by the starter library.
