Crossplatform WebDav storage ( Server-Side implementation ) , designed at first as transparent File-Like storage for J2ee-applications.

Support on Google-App-Engine && Tomcat (tested with 6.x). Generally it map Dav-content into _JSR-107_ provider.

### For GAE ###
google-memcache implementation.

### For Java Appserver aka Tomcat ###
FileSystem Cache.

## so - the main **(and unique!)** key-feature is NO\_PERSISTENCE\_AT\_ALL ##

..or in anothe words any stored object will be keeped as long as CACHED-object. JSR-107 rulse will be performed for Garbage-Collecting.



---


Known Clients (test compleeted):

- Windows (http://help.wildapricot.com/display/DOC/Setting+Up+WebDAV+in+Windows+XP)

- Linux (sudo mount -t davfs  http://localhost:8888/ca  /media/Dav@GAE )

- Total Commander ( 7.x)

- Java ( org.apache.maven.wagon.wagon-webdav-1.0-beta-2 )


[Live Demo Example @ GAE](https://davrepo.appspot.com/ca/) (don't expect Rich GUI ! ;)