/**
*  Insteon Dimmer Child
*
*  Copyright 2016 DMaverock
*
*  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License. You may obtain a copy of the License at:
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
*  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
*  for the specific language governing permissions and limitations under the License.
*
*/
definition(
    name: "Insteon Dimmer Child",
    namespace: "DMaverock",
    author: "DMaverock",
    description: "Child Insteon Dimmer SmartApp",
    category: "My Apps",
    iconUrl: "http://cdn.device-icons.smartthings.com/Lighting/light11-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Lighting/light11-icn@2x.png",
    iconX3Url: "http://cdn.device-icons.smartthings.com/Lighting/light11-icn@2x.png")


preferences {
    page(name: "mainPage", title: "Install Insteon Dimmer Device", install: true, uninstall:true) {
        section("Insteon Dimmer Name") {
            label(name: "label", title: "Name This Insteon Dimmer Device", required: true, multiple: false, submitOnChange: true)
        }
        section("Add an Insteon Dimmer Device") {
			input("InsteonID","string", title: "Insteon Device ID", description: "Please enter your Insteon Device's ID", required:true, submitOnChange: true, displayDuringSetup: true)           
        	input("InsteonIP","string", title: "Insteon Hub IP", description: "Please enter your Insteon Hub's IP Address", required:true, submitOnChange: true, displayDuringSetup: true)           
        	input("port","string", title: "Insteon Hub Port", description: "Please enter your Insteon Hub's Port Number", required:true, submitOnChange: true, defaultValue: "25105", displayDuringSetup: true)           
        	input("InsteonHubUsername","string", title: "Insteon Hub Username", description: "Please enter your Insteon Hub's Username", required:true, submitOnChange: true, displayDuringSetup: true)           
        	input("InsteonHubPassword","password", title: "Insteon Hub Password", description: "Please enter your Insteon Hub's Password", required:true, submitOnChange: true, displayDuringSetup: true)           
        
            }
        section("Hub Settings"){
        	input("hubName", "hub", title:"Hub", description: "Please select your Hub", required: true, displayDuringSetup: true)
        }
    }
    
}

def installed() {
    log.debug "Installed"

    initialize()
}

def updated() {
    log.debug "Updated"

    unsubscribe()
    initialize()
}

def initialize() {
    log.debug "Values are: $InsteonID -- $InsteonIP -- $port -- $InsteonHubUsername -- $InsteonHubPassword -- $hubName"
    log.debug "State DNI is " + state.deviceNetworkId

    state.InsteonID = InsteonID
    state.InsteonIP = InsteonIP
    state.port = port
    state.InsteonHubUsername = InsteonHubUsername
    state.InsteonHubPassword = InsteonHubPassword    
    state.hubName = hubName
    
    try {
        def DNI = (Math.abs(new Random().nextInt()) % 99999 + 1).toString()
        def insteon = getChildDevices()
        //DNI = insteon[0].deviceNetworkId
        //log.debug "Current DNI is $DNI"
        if (insteon) {
        	log.debug "found ${insteon.displayName} with id $DNI already exists"
            insteon[0].configure()
        }
        else {
        	def childDevice = addChildDevice("DMaverock", "Insteon Dimming Device (LOCAL)", DNI, null, [name: app.label, label: app.label, completedSetup: true])
            log.debug "created ${insteon.displayName} with id $DNI"
        }
    } catch (e) {
    	log.error "Error creating device: ${e}"
    }
}

def uninstalled() {
    removeChildDevices(getChildDevices())
}

private removeChildDevices(delete) {
    delete.each {
        deleteChildDevice(it.deviceNetworkId)
    }
}
/*
def getVerifiedDevices() {
    getDevices().findAll{ it?.value?.verified == true }
}

def getDevices() {
    state.devices = state.devices ?: [:]
}
*/