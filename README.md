PI_GetSessionKeyMapping
=======================

PI mapping to process a message with session key authentication.

### Overview
The mapping program is implemented with class GetSessionKeyMapping. This creates the necessary dependencies and starts processing via the appropriate concrete SessionMessage class.
An abstract factory is used to create the required implementation.
The template pattern is used to allow users of this mapping to provide their own logic to specify:
 - the payload of the session key request message
 - how to extract the session key from the response message
 - how to build the final payload to the receiving system (e.g. add the session key to a field, header field, asma, etc).

### Example implementations provided
 - SessionMessageIdentityImpl [simply copy the input payload to the output and log the determined session key - used for testing]
 - SessionMessagePayloadImpl [copy input payload to output and insert the session key into a specified field]
 - SessionMessageAddToPayloadImpl [copy input payload to output and add a new payload field for the session key]
 - SessionMessageSoapHeaderImpl [copy input payload to output and add a soap header field for the session key]
 - SessionMessageASMAImpl [copy input payload to output and create a Dynamic Configuration parameter (ASMA)]

### Notes
 - The SOAP Header implementation requires the PI SOAP channel to be run in NOSOAP mode.
 - The ASMA implementation allows you to use the AXIS receiver adapter for your message and insert the Dynamic Configuration attribute into the HTTP header (e.g. a cookie).
 - For a detailed walk-through of this solution on Evernote: https://www.evernote.com/shard/s4/sh/407418fc-0dd8-4b89-9f55-308d9820093c/f821979213af65d7dc7426fc2b708edd

### Configuration in PI
This mapping is loaded into PI as Imported Archive.
The PI operation mapping references the java class inside the imported archive. A few mapping parameters are required at runtime as follows:
 - BUSINESS_COMPONENT [Business Component for looking up the PI Communication Channel]
 - BUSINESS_COMPONENT_CHANNEL [PI Communication Channel name]
 - MAPPING_TYPE [Used to determine which implementation to use - handled by the Abstract Factory]
 - DC_NAMESPACE [Dynamic Configuration Namespace - used by the ASMA implementation]
 - DC_NAME [Dynamic Configuration Name - used by the ASMA implementation].


### Contributions
Please ensure that any code changes or additional temnplate pattern implementations are covered by unit tests. This project uses JUnit and Mockito.
