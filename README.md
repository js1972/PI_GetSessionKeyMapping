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


### Notes
 - The SOAP Header implementation requires the PI SOAP channel to be run in NOSOAP mode.
 -  All scenario's write the session key to an ASMA (or Dynamic Configuration entry). This can be used if you wish to make use of the session key in a channel (axis) to set a http cookie for example.
 - For a detailed walk-through of this solution on Evernote: https://www.evernote.com/shard/s4/sh/407418fc-0dd8-4b89-9f55-308d9820093c/f821979213af65d7dc7426fc2b708edd

### Configuration in PI
This mapping is loaded into PI as Imported Archive.
The PI operation mapping references the java class inside the imported archive. A few mapping parameters are required at runtime as follows:
 - BUSINESS_COMPONENT [Mandatory - Business Component for looking up the PI Communication Channel]
 - BUSINESS\_COMPONENT\_CHANNEL [Mandatory - PI Communication Channel name]
 - MAPPING_TYPE [Mandatory - Used to determine which implementation to use - handled by the Abstract Factory]
 - DC\_NAMESPACE [Mandatory - Dynamic Configuration Namespace - used by the ASMA implementation]
 - DC\_NAME [Dynamic Configuration Name - used by the ASMA implementation].
 - FIELD\_NAME [This is used in building the request message to the receiving system. For the SET\_FIELD scenario it specifies the field which is set with the session key. For the ADD_FIELD scenario it is used to specify a field at the same node-level where you would like the new field added.]
 - NEW\_SESSIONID\_FIELD\_NAME [Specifies the new field name in the ADD\_FIELD scenario. It is placed at the same node level as the FIELD\_NAME element.]
 - SESSION\_KEY\_RESPONSE\_FIELD [Mandatory - This is the name of the field in the session key web service response that contains the actual session key.]
 - SOAP\_HEADER\_NODE [This is used in the SessionMessageSoapHeaderImpl to specify the field names in the soap header as follows: \<SOAP\_HEADER\_NODE\>\<FIELD\_NAME\>--sessionkey--\</FIELD\_NAME\>\</SOAP\_HEADER\_NODE\>]


### Contributions
Please ensure that any code changes or additional template pattern implementations are covered by unit tests. This project uses JUnit and Mockito.
