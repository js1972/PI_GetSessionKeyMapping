PI_GetSessionKeyMapping
=======================

This eclipse project contains two PI java mappings as sample implementations of how to manage session key authentication with a web service. Each calls into the PI_SessionKeyLib project to do the work of retrieving the session id and then logging off (if requred).

### Overview
1. GetSessionKeyMapping. This mapping program reads a set of parameters from the operation mapping configuration, then builds a string to be used as the request payload to get a session id. A Factory Method is provided to pass this information into the PI_SessionKeyLib class library, which will derive the session id and set it as a Dynamic Configuration attribute on the message.
2. LogoffSessionKeyMapping. This mapping is essentially the same as the above, except the logoff handler is retrieved instead to process a logoff command. If logoff is not explicitly required for the service then this mapping can be ignored.

### Parameters
Mapping paramerers are configured to deternine the necessary values to pass the PI_SessionKeyLIb library:
 - MAPPING_TYPE - this specifies how the session key is to be sent to the web service (inserted into an existing field; added as a new field; added as a soap header field; or simple set as a Dynamic Configuration attribute (which is done in all cases anyway). Allowed values are: ADD\_FIELD, SET\_FIELD, SOAP\_HEADER, LOGOFF. Anything else triggers the identity mapping function which does nothing to the payload.
 - DC_NAMESPACES - this is the namespace for the Dynamic Configuration attribute that is created to hold the session id.
 - DC_NAME - this is the name of the Dynamic Configuration attribute as mentioned above.
 - BUSINESS\_COMPONENT, BUSINESS\_COMPONENT\_CHANNEL and BUSINESS\_COMPONENT\_CHANNEL\_LO - These are to define the Communication Channel to use for getting the session id and the one suffixed in "_LO" is for logoff (if req'd).
 - FIELD\_NAME - This is to specify the payload element name that is to be filled with the session id when using SET\_FIELD. For ADD\_FIELD it is used to speicfy the payload element for which the new field is a sibling.
 - NEW\_SESSIONID\_FIELD\_NAME - For the ADD_FIELD type this specifies the name of the new element. For the SOAP\_HEADER type it specifies the structure of the elemends to be created in the header based on "node/element". The substring before the forward-slash is the new xml node that is created in the soap header. The substring after the forward-slash is the name of the element that is created within the node.
 - SESSION\_KEY\_RESPONSE\_FIELD - this is used to specify the field to search for in the response to the session id request to grab the actual session id.


For a detailed walk-through of this solution on Evernote: https://www.evernote.com/shard/s4/sh/407418fc-0dd8-4b89-9f55-308d9820093c/f821979213af65d7dc7426fc2b708edd


### Contributions
Please ensure that any code changes or additional template pattern implementations are covered by unit tests. This project uses JUnit and Mockito.
