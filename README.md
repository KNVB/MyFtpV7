# MyFtpV7
Java based FTP server using Netty Framework

Configuration File location
/conf/server-config

The file "user.db" is created by sqlite DB browser.

To start the server, just execute the com.myftpserver.MyFtpServer 

The com.myftpserver.abstracts.FileManager class is an abstract class that provide method for FTP user to manipulate the file or folder in FTP server.
User can extends this abstracts class for their own file management implementation.
 
The com.myftpserver.abstracts.UserManager class is an abstract class that provide method for FTP user management.
User can extends this abstracts class for their own user management implementation.

The com.myftpserver.abstracts.FtpServerConfig class is an abstract class that provide method for FTP Server Configuration management.
User can extends this abstracts class for their own Server Configuration management implementation.