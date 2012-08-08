confluence2mw
=============

Takes a directory of confluence files from loki, downloads the converted mediawiki markup to the local HDD
Finds all of the corresponding attachments from loki and downloads them under into a resources drive on the local HDD

The project is easily compiled in Eclipse using java version 5 and up, all required libraries are included in the source.
Version number, source, local HDD directory are configurable in the convert.properties file.

To run the project inEclipse, follow these steps:

1. Clone the project from GitHub
2. In Eclipse go to File->Import, then General->Existing Projects into Workspace and then navigate to the cloned project directory
3. Change version number and source in the convert.properties file to your specifications.
4. Log onto Loki from your browser using your credentials.
5. Run the project or Run As JUnit Test if you want to run the Junit Tests.

Additionally, the properties file can be overridden by command line arguments with the following options:

-v Version
-s Source
-f Local File (Converts a local .confluence file)
-u URL (converts a .confluence at specified URL)

Currently there are bugs rooting from the original javascript app this was converted from (http://www.infinality.net/files/confluence-to-mediawiki-converter/)
Additionally there are some issues where different file structure on Loki and confluence syntax are used for different products.