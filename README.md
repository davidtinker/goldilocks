# Goldilocks

Raspberry Pi based temperature logging and/or control software for home brewing. Reads 1-wire DS18B20 temperature
probes and optionally enables/disables heating element(s) using output pin(s). The user interface is web based and
mobile-friendly so you can monitor and control your brewing over Wifi using a phone or tablet. The temperature
control algorithm automatically tunes itself.

![Screenshot](docs/img/goldilocks_screenshot.png?raw=true)

## Installation

I am using Raspbian installed via NOOBS on my Pi but Goldilocks should run on anything with Java 8 installed. For
Debian based systems (e.g. Raspbian) try the following instructions.

First install [Pi4j](http://pi4j.com/install.html). This is very simple:

    $ curl -s get.pi4j.com | sudo bash
    
If you aren't happy running a random script from the internet on your Pi or have issues please refer to the Pi4j
site. Now download and install Goldilocks:

    $ sudo bash
    # wget https://github.com/davidtinker/goldilocks/releases/download/v1.0.0-beta/goldilocks-1.0.0.deb
    # dpkg -i goldilocks-1.0.0.deb

Browse to http://10.0.0.103:5050/ (replace 10.0.0.103 with the IP address of your Pi) and you should see the 
Goldilocks UI. It might take a little while to come up.

### Upgrading

Uninstall goldilocks and then re-install use a newer .deb file:

    $ sudo bash
    # dpkg -r goldilocks

### Hardware

Goldilocks is known to work with the following hardware:

  - [Raspberry Pi Model B](https://www.raspberrypi.org/) tiny Linux computer
  - [DS18B20 1-wire temperature probes](http://www.communica.co.za/Catalog/Details/P3426552881)
  - A heating element attached to one of the output pins of the Pi, via a solid state relay

There are lots of guides online on how to get these working:

  - [DS18B20+ One Wire Digital Temperature Sensor and the Raspberry Pi](http://www.modmypi.com/blog/ds18b20-one-wire-digital-temperature-sensor-and-the-raspberry-pi)

### Configuration

Click "Goldilocks" on the title bar to set the name of your brewery and change to Fahrenheit if you don't like
metric. Click the chart area to open the "Chart Settings" dialog, then click the "Add Control" button.
Click "Configure Control" to open the "Control Settings" dialog.

Each control has a name, optional temp probe, optional output pin and a color. Controls with only a temp probe
configured just log temperature (e.g. a mash tun). Controls with a temp probe and output pin configured can be
set to automatically raise the temperature until a target temp is reached (e.g. a hot liquor tank with electric
heating element). Controls can also have just an output pin (e.g. for turning a pump on/off).

The temperature probes listed in the drop down are discovered by listing /sys/bus/w1/devices. If the drop down is
empty then likely your probe(s) are not being picked up by the Pi (check your wiring etc.).

You can re-configure controls by clicking the name next to the chart. If the control has an output then click the 
"OFF/ON" label to turn the output on or off and to enter a target temperature for automatic temperature control. 

### Warning

This is a piece of software and will definitely have bugs. Use this software at your own risk. Controlling heating
elements with software is a dangerous business. Some safety hints:

  - Don't leave your heater element connected to your solid state relay if there isn't water in your HLT
  - Don't leave your brewery alone while the water in the HLT is heating up
  - Don't leave your brewery alone if your heater element is connected to the system

## Building

The server side of the application is written in Groovy and Java 8 using the [Ratpack](http://ratpack.io/) web 
framework. The client side is a single-page Javascript application using [Node.js](https://nodejs.org/),
[Browserify](http://browserify.org/), [ReactJS](http://facebook.github.io/react/) with 
[Flux](https://facebook.github.io/flux/). 
This was my first ReactJS application and I am planning on rewriting the client using ReactJS with other better 
tools.

### Client

You need to install [Node](https://nodejs.org/). Then do: 

    $ cd src/ratpack/public
    $ npm install
    $ npm start
    
This will install all the Goldilocks dependencies and start a script that buildles the application into a single js
file. When .js or .jsx files change the application is automatically rebuilt. There is no live reloading however
so you still need to reload the page in your browser.
 
### Server
 
The server is built using [Gradle](http://gradle.org/) and you need version 2.3 or newer. You also need Java 8. To
run the server locally create /var/lib/goldilocks and give your user write access. Then do:

    $ gradle run
    
This starts the server with the -DfakePi=true switch for development on your Mac or Linux machine. The 'fake' Pi has
a couple of temperature probes attached and the heater pin is GPIO_17. Browse to http://127.0.0.1:5050/ to see it 
in action.

## Packaging

You can build a fat jar and run that directly:

    $ gradle shadowJar
    $ java -DfakePi=true -jar build/libs/goldilocks-*.jar 

To build the .dev package:

    $ gradle deb

## License

GNU General Public License (GPL) version 3.0
