/**
 *  Insteon On/Off Device SA (LOCAL)
 *
 *  Copyright 2014 patrick@patrickstuart.com
 *  Updated 1/4/15 by goldmichael@gmail.com
 *  Updated 6/26/16 by maverickd@live.com
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
 
metadata {
	definition (name: "Insteon On/Off Device SA (LOCAL)", namespace: "DMaverock", author: "patrick@patrickstuart.com/tslagle13@gmail.com/goldmichael@gmail.com/maverickd@live.com") {
		capability "Switch"
		capability "Sensor"
		capability "Actuator"        
        capability "Polling"
        capability "Refresh"
	}
/*
    preferences {
    input("hostLocal", "string", title:"Insteon IP Address", description: "Please enter your Insteon Hub IP Address", defaultValue: "192.168.1.2", required: true, displayDuringSetup: true)
    input("port", "string", title:"Insteon Port", description: "Please enter your Insteon Hub Port", defaultValue: 25105, required: true, displayDuringSetup: true)
    input("InsteonID", "string", title:"Device Insteon ID", description: "Please enter the devices Insteon ID - numbers only", defaultValue: "1E65F2", required: true, displayDuringSetup: true)
    input("InsteonHubUsername", "string", title:"Insteon Hub Username", description: "Please enter your Insteon Hub Username", defaultValue: "michael" , required: true, displayDuringSetup: true)
    input("InsteonHubPassword", "password", title:"Insteon Hub Password", description: "Please enter your Insteon Hub Password", defaultValue: "password" , required: true, displayDuringSetup: true)
   }
*/
	simulator {
		// status messages
		status "on": "on/off: 1"
		status "off": "on/off: 0"

		// reply messages
		reply "zcl on-off on": "on/off: 1"
		reply "zcl on-off off": "on/off: 0"
	}

	// UI tile definitions    
    tiles(scale: 2) {
		multiAttributeTile(name: "switch", type: "lighting", width: 6, height: 4, canChangeIcon: true, canChangeBackground: true) {
			tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
        			attributeState "off", label: "off", action: "switch.on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn"
			      	attributeState "on", label: "on", action: "switch.off", icon: "st.switches.light.on", backgroundColor: "#79b821", nextState: "turningOff"
                  	attributeState "turningOff", label: "turningOff", action: "switch.on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn"
			      	attributeState "turningOn", label: "turningOn", action: "switch.off", icon: "st.switches.light.on", backgroundColor: "#79b821", nextState: "turningOff"
            		}
            		//tileAttribute("device.level", key: "SLIDER_CONTROL") {
                  	//	attributeState "level", action:"switch level.setLevel"
            		//}
            		//tileAttribute("level", key: "SECONDARY_CONTROL") {
                  	//	attributeState "level", label: 'Light dimmed to ${currentValue}%'
            		//}    
		}
		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 6, height: 2) {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
      
		main "switch"
		details(["switch","attDimRate", "refresh", "attDimOnOff"])
	}
}

// handle commands
def on() {
	
    sendEvent(name: "switch", value: "on")
    sendEvent(name: "level", value: "100")
    def host = parent.InsteonIP
    def port = parent.port
  
	def path = "/3?0262" + parent.InsteonID  + "0F11FF=I=3"
    log.debug "OnPath is: $path"

    def userpassascii = parent.InsteonHubUsername + ":" + parent.InsteonHubPassword
	def userpass = "Basic " + userpassascii.encodeAsBase64().toString()
    def headers = [:] //"HOST:" 
    headers.put("HOST", "$host:$port")
    headers.put("Authorization", userpass)

    def method = "GET"
    
    try {
    def hubAction = new physicalgraph.device.HubAction(
    	method: method,
    	path: path,
    	headers: headers
        )
        return hubAction
    }
    catch (Exception e) {
    log.debug "Hit Exception on $hubAction"
    log.debug e
    }    	
}

def off() {
	
    sendEvent(name: "switch", value: "off")
    sendEvent(name: "level", value: "0")
    def host = parent.InsteonIP
    def port = parent.port
  
	def path = "/3?0262" + parent.InsteonID  + "0F1300=I=3"
    log.debug "OffPath is: $path"

    def userpassascii = parent.InsteonHubUsername + ":" + parent.InsteonHubPassword
	def userpass = "Basic " + userpassascii.encodeAsBase64().toString()
    def headers = [:] //"HOST:" 
    headers.put("HOST", "$host:$port")
    headers.put("Authorization", userpass)

    def method = "GET"
    
    try {
    def hubAction = new physicalgraph.device.HubAction(
    	method: method,
    	path: path,
    	headers: headers
        )
        return hubAction
    }
    catch (Exception e) {
    log.debug "Hit Exception on $hubAction"
    log.debug e
    }    	
}

def setLevel(level) {        
    log.debug "Dimming to " + level
    sendEvent(name: "level", value: level)
    
    def value = (level * 255 / 100)
    def lvl = hex(value)       
    def host = parent.InsteonIP
    def port = parent.port
  
	def path = "/3?0262" + parent.InsteonID  + "0F21${lvl}=I=3"
    log.debug "DimPath is: $path"

    def userpassascii = parent.InsteonHubUsername + ":" + parent.InsteonHubPassword
	def userpass = "Basic " + userpassascii.encodeAsBase64().toString()
    def headers = [:] //"HOST:" 
    headers.put("HOST", "$host:$port")
    headers.put("Authorization", userpass)

    def method = "GET"
    
    try {
    def hubAction = new physicalgraph.device.HubAction(
    	method: method,
    	path: path,
    	headers: headers
        )
        return hubAction
    }
    catch (Exception e) {
    log.debug "Hit Exception on $hubAction"
    log.debug e
    }    
}

def refresh() {
	
    log.debug "Start Refresh"
	def InsteonHubUsername = parent.InsteonHubUsername
    def InsteonHubPassword = parent.InsteonHubPassword
    def InsteonID = parent.InsteonID
    def host = parent.InsteonIP
    def port = parent.port
    def hosthex = convertIPToHex(host)
    def porthex = Long.toHexString(Long.parseLong((port)))
    if (porthex.length() < 4) { porthex = "00" + porthex }

    def currDNI = device.deviceNetworkId
    log.debug "currDNI is $currDNI"
    log.debug "InsteonID is $InsteonID, host is $host, port is $port"
    //log.debug "doRefresh Start"
    //def del = getChildDevices().findAll { settings.devices.contains(currDNI) }
    //parent.doRefresh()
    //log.debug "doRefresh End"
    
    //httpGet("http://${InsteonHubUsername}:${InsteonHubPassword}@" + "${host}" + ":" + "${port}" + "//3?0262${InsteonID}0F1900=I=3") {response ->     
    //    def content = response.data
    //    log.debug content
    //} 
    log.debug "getStatus"
    try {
	getStatus(1)
   	}
    catch (Exception e) {
    	log.debug "Error with getStatus"
    }
    log.debug "getStatus done"
    
/*
    log.debug "Command Sent: " + "19" + ", " + "00"
   
    def i = Math.round(convertHexToInt(level) / 256 * 100 )
	sendEvent( name: "level", value: i )
*/
    //log.debug "Port in Hex is $porthex"
    //log.debug "Hosthex is : $hosthex"    
    //def deviceNetworkId = "$hosthex:$porthex" 
    //def instID = "$InsteonID"
    //instID = instID.toLowerCase()
    //log.debug "ID is $instID"
    //def deviceNetworkId = "$instID:$porthex" 
    //log.debug "newDNI is $deviceNetworkId"
    //device.deviceNetworkId = "$deviceNetworkId"
    
    //sendRefresh()    
    //def result = buffStatus()        
    //return result
    
    //device.deviceNetworkId = currDNI
    //log.debug "After DNI is " + device.deviceNetworkId    
    log.debug "End refresh"
	      
}

def getStatus(num) {
	
    def InsteonHubUsername = parent.InsteonHubUsername
    def InsteonHubPassword = parent.InsteonHubPassword
    def InsteonID = parent.InsteonID
    def host = parent.ExternalIP
    def port = parent.port    

	if(num < 6)
    {    	
		httpGet("http://${InsteonHubUsername}:${InsteonHubPassword}@" + "${host}" + ":" + "${port}" + "/buffstatus.xml") {response ->             
        def content = response.data
        log.debug content
            	
        if(content.text().length() == 100)
        {
            log.debug content.text().substring(22,28)
            if(content.text().substring(22,28) == InsteonID)
            {
                log.debug content.text().substring(38,42)
                if(content.text().substring(38,40) == '00' || content.text().substring(38,40) == '01')
                {
                    log.debug "switch is off"
                    sendEvent(name: "switch", value: "off");
                    sendEvent(name: "level", value: "0" )
                }
                else
                {                	
                    def i = Math.round(convertHexToInt(content.text().substring(38,40)) / 256 * 100 )
					sendEvent(name: "level", value: i )
                    log.debug "switch is on and at level " + i
                    sendEvent(name: "switch", value: "on");                    
                }
            }
			
            else
            {
                //sendCmd("19", "00")
                num = num + 1
                getStatus(num)
                log.debug "DeviceID is different"
            }
        }        
        else
        {
            //sendCmd("19", "00")        
            num = num + 1
            getStatus(num)
            log.debug "Unexpected Buffer Length (should be 100)"
        }        
      }
   }
   else { log.debug "Timeout, too many retries (5)" }        
}

def sendRefresh() {

	log.debug "Refreshing Status"    
    def host = parent.InsteonIP
    def port = parent.port
    
    def path = "/3?0262" + parent.InsteonID  + "0F1900=I=3"
    log.debug "RefreshPath is: $path"

    def userpassascii = parent.InsteonHubUsername + ":" + parent.InsteonHubPassword
	def userpass = "Basic " + userpassascii.encodeAsBase64().toString()
    def headers = [:] //"HOST:" 
    headers.put("HOST", "$host:$port")
    headers.put("Authorization", userpass)

    def method = "GET"
    
    try {
    def hubAction = new physicalgraph.device.HubAction(
    	method: method,
    	path: path,
    	headers: headers
        )
        return hubAction
    }
    catch (Exception e) {
    log.debug "Hit Exception on $hubAction"
    log.debug e
    }
}

def buffStatus() {
    
    def host = parent.InsteonIP
    def port = parent.port
    def path = "/buffstatus.xml"
    log.debug "buffPath is $path"    
    
    def instID = parent.InsteonID
    instID = instID.toLowerCase()
    log.debug "instID is $instID"
    
    def hosthex = convertIPToHex(host)
    def porthex = Long.toHexString(Long.parseLong((port)))
    if (porthex.length() < 4) { porthex = "00" + porthex }
    
    def userpassascii = parent.InsteonHubUsername + ":" + parent.InsteonHubPassword
	def userpass = "Basic " + userpassascii.encodeAsBase64().toString()
    def headers = [:] //"HOST:" 
    headers.put("HOST", "$host:$port")
    headers.put("Authorization", userpass)

    def method = "GET"
    
    log.debug "hubAction buffStatus"
    try {
    def hubAction = new physicalgraph.device.HubAction(
    	method: method,
    	path: path,
    	headers: headers
        ) 
        return hubAction
    }
    catch (Exception e) {
    log.debug "Hit Exception on $hubAction"
    log.debug e
    }    
}

def poll() {
	getStatus(1)
}

def parse(description) {
   
    def msg = parseLanMessage(description)

    def headersAsString = msg.header // => headers as a string
    def headerMap = msg.headers      // => headers as a Map
    def body = msg.body              // => request body as a string
    def status = msg.status          // => http status code of the response
    def json = msg.json              // => any JSON included in response body, as a data structure of lists and maps
    def xml = msg.xml                // => any XML included in response body, as a document tree structure
    def data = msg.data              // => either JSON or XML in response body (whichever is specified by content-type header in response)
    
    log.debug "parsing"
    
    log.debug "status: $status -- body: $body -- json: $json -- xml: $xml -- data: $data"
    //log.debug "DNI is " + device.deviceNetworkId
    
    //log.debug "left xml is " + left(xml,23)

}

// gets the address of the hub
private getCallBackAddress() {
    return device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")
}

// gets the address of the device
private getHostAddress() {
    def ip = getDataValue("ip")
    def port = getDataValue("port")

    if (!ip || !port) {
        def parts = device.deviceNetworkId.split(":")
        if (parts.length == 2) {
            ip = parts[0]
            port = parts[1]
        } else {
            log.warn "Can't figure out ip and port for device: ${device.id}"
        }
    }

    log.debug "Using IP: $ip and port: $port for device: ${device.id}"
    return convertHexToIP(ip) + ":" + convertHexToInt(port)
}

private Integer convertHexToInt(hex) {
    return Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) {
    return [convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

private hex(value, width=2) {
	def s = new BigInteger(Math.round(value).toString()).toString(16)
	while (s.size() < width) {
		s = "0" + s
	}
	s
}

private String convertIPToHex(ipAddress) {
	return Long.toHexString(converIntToLong(ipAddress));
}

private Long converIntToLong(ipAddress) {
	long result = 0
	def parts = ipAddress.split("\\.")
    for (int i = 3; i >= 0; i--) {
        result |= (Long.parseLong(parts[3 - i]) << (i * 8));
    }

    return result & 0xFFFFFFFF;
}

def installed() {
	configure()
}

def updated() {
	configure()
}

def configure() {
	log.debug "Executing 'configure'"
    //state.hubName = parent.hubName
    //sendEvent(name:"switch", value: "on")
    refresh()
}