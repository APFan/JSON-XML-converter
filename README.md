# JSON-XML-converter
A program which converts text from one format to another.
To achieve consistency between the two, XML attributes are perceived as key-value pairs with the key name "@attr" and tag body is a key-value pair with the key name "#objname", all of which are in "objname":{attributes+body} key-value pair. JSON arrays are also supported, in XML they are seen as a sequence of tags with the same name.

Usage: java Main *FILENAME*
