# WEARHACKS LESHAN LIBRARY
[Eclipse Leshan](https://eclipse.org/leshan) is an OMA Lightweight M2M server and client Java implementation. 
This repository is a clone of the Leshan source code with a few modifications.
The modifications allow Leshan to be used as a LWM2M client with Nokia's [Motive® Connected Device Platform (CDP)](https://networks.nokia.com/solutions/connected-device-platform).

## BEFORE YOU START
[What is OMA LWM2M?](http://www.openmobilealliance.org/wp/overviews/lightweightm2m_overview.html)  
[Introduction to LWM2M](http://fr.slideshare.net/zdshelby/oma-lightweightm2-mtutorial)  
[LWM2M object conventions](http://www.openmobilealliance.org/wp/OMNA/LwM2M/LwM2MRegistry.html)  

### WHAT ELSE DO YOU NEED?
[Eclipse Java IDE](https://eclipse.org/downloads/packages/eclipse-ide-java-developers/neon2) (Or your preferred editor/IDE)    
[Apache Maven build manager](https://maven.apache.org/) (Can be installed within Eclipse or via the terminal if you're using a different IDE)    

## CONNECTING TO CDP - THE BASICS
[Video Tutorial](https://google.ca) 

- Start by downloading the ZIP contents of this repository, and extracting them to a folder somewhere on your computer.  
- Open a terminal and navigate into the folder.   
- Build the project by running the command `mvn clean install`  

Before we try to connect our client to CDP we have to log in and let CDP know that we will be connecting a LWM2M device to it under a specific name.  

- To do this, log into your CDP instance by going [here](http://52.9.139.199:8180/ui) and entering your credentials.  
- Next open the Devices tab on the left and click Add a Device.   
- Add the device name in the Device ID textbox. (The placeholder text is "Serial Number | MAC address")
- In the manufacturer dropdown select "Generic"
- In the model dropdown select "LWM2M Generic Device" and click save  

You will now be able to connect a client to CDP under the device name you specified. Next,

- Run the demo client by running the command `java -Dkludge=true -jar leshan-client-demo/target/leshan-client-demo-*-SNAPSHOT-jar-with-dependencies.jar -u 59.2.139.199:5684 -n <Device Name>` 

Your client should now proceed to connecting to CDP. To verify,

- In the Devices tab on the left click on View Device.
- Search for your device by changing the search criteria to "Find All Devices" and clicking Search.
- Click on your device name
- Click Additional Info and verify that the "updated" time matches the time you connected your client to CDP. 

## INTERACTING WITH THE OBJECTS ON YOUR CLIENT
[Video Tutorial](https://google.ca)  

CDP requests and writes information by queuing and executing "Operations" on your device from the device page. These operations are based on "Actions". Each action is essentially just a named LWM2M request. Before we can do this, we need to create actions for the new device we created. The client demo code contains a few test objects already. Remember that objects in LWM2M protocol have pre-defined resources and paths. Read through the links on LWM2M near the top of the page if you want to know more about how these objects and paths work. Our demo code has three objects: A MyDevice object, a MyLocation object and a RandomTemperatureSensor object. Let’s create an action to request the device manufacturer from the MyDevice object.  

