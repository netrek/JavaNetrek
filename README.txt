 README for version 0.9.6 Beta by Darrell Root
 
 I've taken Robert Temple's old JTrek code from 1998 and am attempting to update
 it to run on recent versions of Java.  Most of the code is cut-and-pasted from
 Robert's source code. 

 0.9.6 update: multi-button mouse works!  But we have high cpu utilization and need profiling.

 Long live Netrek!  - Darrell      feedback@networkmom.net
 
 
 
                           README

                           JTrek 
           a Netrek client for the Java(TM) Platform
                     version 0.9.4 Beta

                      by Robert Temple

                      November 17, 1998

-----------------------------------------------------------------------
NOTICE
-----------------------------------------------------------------------

No representations are made about the suitability of this software for
any purpose.  It is provided "as is" without express or implied warranty.

JTrek was written entirely by Robert Temple on his own time.  In no way
is this software associated with Robert Temple's employer, Starwave
Corporation.  Starwave is not responsible for any part of this software,
nor is Starwave responsible for anything that happens as the result of
using this software.

Java and all Java-based marks are trademarks or registered trademarks of
Sun Microsystems, Inc. in the United States and other countries. Robert
Temple is independent of Sun Microsystems, Inc.

-----------------------------------------------------------------------
Contents
-----------------------------------------------------------------------

   Overview of JTrek
   Acknowlegments
   System requirements
   Installing JTrek
   Running JTrek
   Changes
   Things which are not yet implemented
   Known bugs
   Comments, Bug reports
   resource/properties file changes 
   Other information

-----------------------------------------------------------------------
Overview of JTrek
-----------------------------------------------------------------------

JTrek is the first Netrek client written entirely in 100% Java. This is
a alpha release of the software, meaning that there parts of the
software that are not yet implemented, and there are many bugs.

Most of the code for this software was ported over from the COW and 
Paradise clients.

Look for new releases at: http://www.starwave.com/people/robertt/JTrek

-----------------------------------------------------------------------
Acknowlegments
-----------------------------------------------------------------------

People who made it possible for me to do this:

Jason Herring, who helped me set up my own server, so that I could test
this thing.  Brad Morrey, who set up a server at Berkeley and helped
me test the client as an applet.

People who worked on the COW client or some other part of Netrek:
Chris Guthrie, Ed James, Scott Silvey, and Kevin Smith,            
Tedd Hadley, Andy McFadden, Eric Mehlhaff, J. Mark Noworolski,     
Nick Trown, Lars Bernhardsson, Sam Shen, Rick Weinstein,           
Jeff Nelson, Jeff Waller, Robert Kenney, Steve Sheldon,            
Jonathan Shekter, Dave Gosselin, Heiko Wengler, Neil Cook, Kurt Siegl 
and many others.

People who worked the Paradise client:
Larry Denys, Kurt Olsen, Brandon Gillespie

-----------------------------------------------------------------------
System requirements
-----------------------------------------------------------------------

JTrek requires that a Java 1.1 compatible version of a Java virtual
machine be installed on your machine.  For information on where to get
this software, see http://www.javasoft.com

Internet Explorer 4 for Windows 95/98/NT comes with a Java 1.1 compatible
Java virtual machine.  If you have this, use the execute the msrun.bat
file.

----------------------------------------------------------------------
Installing JTrek
-----------------------------------------------------------------------

To install JTrek, uncompress the archive into a directory. 

-----------------------------------------------------------------------
Running JTrek
-----------------------------------------------------------------------

JTrek requires that both the the standard Java 1.1 libraries, and the
JTrek classes be in the Java classpath.

The main Java class of JTrek is:

  jtrek.Main

To execute JTrek use the following command from the prompt:

  java jtrek.Main -h kirk.ci.houston.tx.us -p 2592
  
-----------------------------------------------------------------------
Setting up JTrek as an applet
-----------------------------------------------------------------------

JTrek was designed to work with IE 4 and Netscape 4 as an applet.  User
properties get saved as cookies.

Copy the applet.html page to your HTTP server.  The HTTP server needs
to be the same server as the netrek server.  Put the JTrek.jar file
in the same directory as the applet.html.

To get sounds to work, Make a sub directory called jtrek in this
directory.  In this directory make another called applet, and then one 
more called sounds.  Put all the .au files there.

Your files system should look like this:
/applet.html
/JTrek.jar
/jtrek/applet/sounds/*.au

Hit applet.html with IE 4 or Netscape 4 or greater and try it out!

-----------------------------------------------------------------------
Changes
-----------------------------------------------------------------------
0.9.4 
fixed bug with distress calls being screwed up.
fixed bug that made people log off on the refit screen when they didn't want to.
added ability to reset stats from MOTD window.
added a metaserver window.
various other minor fixes and enhancements.

0.9.3
added sound support in the applet version
added help window, 
added continuous mouse 
added the macro window
enhanced the applet version of  the applet.

0.9.2 
added RSA
added full color support
added a few others things that I can't remember.

-----------------------------------------------------------------------
Things which are not yet implemented
-----------------------------------------------------------------------

There are a few things that are missing that you are probably used to
in other clients.

Documentation!!!
netstat
TTS

-----------------------------------------------------------------------
Comments, Bug reports
-----------------------------------------------------------------------

Send all comments, requests, bug reports to: robertt@starwave.com

PLEASE, put the word JTREK somewhere is the subject, otherwise I might
ignore the message...

-----------------------------------------------------------------------
resource/properties file changes
-----------------------------------------------------------------------

The default resource file is called "JTrek.props" Inside is all of the
defaults settings of the client.

This file is very similar to the .xtrekrc you are used to.  A few keys
have changed to have consistent naming patterns as other keys.  All 
boolean parameters should be either "true" or "false" - "on", "off", 
"yes", and "no" don't work.  Window geometry is done with pixels 
increments, in the form of "x,y,width,height".

-----------------------------------------------------------------------
Other information
-----------------------------------------------------------------------

JTrek doesn't have extended buttonmaps.  I don't think it is possible 
to implement this in Java because of the way Java handles the 
differences between systems with less then three mouse buttons.

The classes are compiled with optimation turned off, and debugging
turned on.  This should help me track down any bugs that might be out
there.

I've only tested this on Windows NT 4.0 and Solaris.

Never quit the game by using Control-C at the command prompt.  This will 
leave a slot being wasted on the server.

-----------------------------------------------------------------------
Source code
-----------------------------------------------------------------------

There is a link to the latest at: 
http://www.starwave.com/people/robertt/JTrek
