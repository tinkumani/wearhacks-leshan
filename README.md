# WEARHACKS LESHAN LIBRARY
[Eclipse Leshan](https://eclipse.org/leshan) is an OMA Lightweight M2M server and client Java implementation. 
This repository is a clone of the Leshan source code with a few modifications.
The modifications allow Leshan to be used as a LWM2M client with Nokia's [Motive® Connected Device Platform (CDP)](https://networks.nokia.com/solutions/connected-device-platform).

All Source code is property of Eclipse. Thank you to Ramesh Pattabhiraman for the CDP workaround.

## BEFORE YOU START
[What is OMA LWM2M?](http://www.openmobilealliance.org/wp/overviews/lightweightm2m_overview.html)  
[Introduction to LWM2M](http://fr.slideshare.net/zdshelby/oma-lightweightm2-mtutorial)  
[LWM2M object conventions](http://www.openmobilealliance.org/wp/OMNA/LwM2M/LwM2MRegistry.html)  

### WHAT ELSE DO YOU NEED?
[Eclipse Java IDE](https://eclipse.org/downloads/packages/eclipse-ide-java-developers/neon2) (Or your preferred editor/IDE)    
[Apache Maven build manager](https://maven.apache.org/) (Can be installed within Eclipse or via the terminal if you're using a different IDE)    

### CODING WITH ECLIPSE

You need to add the M2_REPO to your java classpath variables.

- run `mvn -Declipse.workspace=<path-to-eclipse-workspace> eclipse:add-maven-repo` OR add it manually in eclipse.

Eclipsify the project files 

- Navigate to the project folder and run `mvn eclipse:ecpise`

## CONNECTING TO CDP - THE BASICS
[Video Tutorial](https://vimeo.com/204568989)

Before anything, we need the code!

- Start by downloading the ZIP contents of this repository, and extracting them to a folder somewhere on your computer.  
- Open a terminal and navigate into the folder.   
- Build the project by running the command `mvn clean install`  

Before we try to connect our client to CDP we have to log in and let CDP know that we will be connecting a LWM2M device to it under a specific name.  

- To do this, log into your CDP instance by going to your CDP instance and entering your credentials.  
- Next open the Devices tab on the left and click Add a Device.   
- Add the device name in the Device ID textbox. (The placeholder text is "Serial Number | MAC address")
- In the manufacturer dropdown select "Generic"
- In the model dropdown select "LWM2M Generic Device" and click save  

You will now be able to connect a client to CDP under the device name you specified. Next,

- Run the demo client by running the command `java -Dkludge=true -jar leshan-client-demo/target/leshan-client-demo-*-SNAPSHOT-jar-with-dependencies.jar -u <CDP IP Address>:5684 -n <Device Name>` 

Its worth noting that once you've built the files with maven you can launch this jar file as a standalone executable from any device that supports java using that command.  

Your client should now proceed to connecting to CDP. To verify,

- In the Devices tab on the left click on View Device.
- Search for your device by changing the search criteria to "Find All Devices" and clicking Search.
- Click on your device name
- Click Additional Info and verify that the "updated" time matches the time you connected your client to CDP. 

### RUNNING THE CLIENT ON A RASPBERRY PI

You have a few options when running the client on a Raspberry Pi:

#### OPTION 1

Simply follow all of the steps in these tutorials on the Raspberry Pi. Do all of your compiling locally. This is the best option if your objects require special inputs from (for example) the pins on the Pi, as your code testing needs to be done on the Pi. 

#### OPTION 2 

Do all of your coding and compiling on a laptop and then SCP the .jar file to your Raspberry Pi and run it. This is the best option if your code doesn't need any special inputs on the Pi and can be directly ported between generic unix machines. You'll have the advantage of doing all of your coding and testing on a faster machine. 

To SCP the .jar file to the Pi

- Make sure SSH and SCP is enabled on your rasberry pi
- Open a terminal on your laptop and type in `scp <path to .jar file> <pi user>@<raspberry pi ip>:<location you want the .jar file>`
- Enter the user password on the Pi. (By default the user is pi and the password is raspberry)

A quick way to SCP files to your Pi is to connect your Pi to your laptop with an ethernet cable and replace the `<raspberry pi ip>` field above with `raspberrypi.local`.

## INTERACTING WITH THE OBJECTS ON YOUR CLIENT

### READING OBJECTS ON YOUR CLIENT
[Video Tutorial](https://vimeo.com/205581549)  

CDP requests and writes information by queuing and executing "Operations" on your device from the device page. These operations are based on "Actions". Each action is essentially just a named LWM2M request. Let's create a few actions.

- Go to the Actions tab on the left and click on Add Action.
- Name your action 

Now here's the important bit. We have to select a primitive for this action. Primitives specify the exact request that the server sends out. Lets start with a basic LWM2M read request. 

- From the primitive dropdown, select "LWM2M.read".
- You'll see a Path textbox appear.

Lets start by accessing the MyDevice Manufacturer information, which is accessed with the path /3/0/0. 

- Enter /3/0/0 into the Path textbox.
- Click save and navigate back to the device's page.
- Click on the Operations tab and find the action you created.
- Click the icon on the right to queue the action

The server will now carry out the specified action. You can go back to the Job Details tab to view the results of the operation. Click on the operation to view details on the response.

### CREATING YOUR OWN OBJECTS 
[Video Tutorial](https://google.ca) (Coming Soon!)

This topic is a bit too complicated to cover on the readme. Watch the video for a quick walkthrough on how to add your own object to the client and how to modify the responses that the server recieves when querying the client. 

After watching the video you'll understand a lot more about how the leshan client uses objects and you'll be able to see how you might leverage the Leshan client and CDP to create and manage interesting devices.

### WRITING AND EXECUTING COMMANDS ON YOUR CLIENT
[Video Tutorial](https://google.ca) (Coming Soon!)

Again, this would be a little too mutch to try to squeeze into a readme. Click the link above to watch a quick video on how you can use CDP to write information and execute commands on your client. 

## USING THE CDP API
[Video Tutorial](https://google.ca) (Coming Soon!)

One of the advantages of using CDP as a management solution is its robust set of API calls. There is API documentation included in this repository. You're just a few HTTP requests away from creating user-facing applications to graph and display information.

## WHAT ARE THE POSSIBILITIES?
[Video Project](https://google.ca) (Coming Soon!)





