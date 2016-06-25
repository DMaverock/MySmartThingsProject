/**
 *  Insteon Dimmer
 *
 *  Author: idealerror
 *  Date: 2015-09-26
 *  Author: DMaverock
 *  Date: 2016-06-17
 *  Changes: Adding Dimming Function for Insteon Lights.  Thanks to jscgs350 for the UI from "My GE Link Bulb"
 */
preferences {
    input("deviceid", "text", title: "Device ID", description: "Your Insteon device ID")
    input("host", "text", title: "External URL", description: "The IP/DNS of your SmartLinc or 2422 Hub")
    input("hostLocal", "text", title: "Local URL", description: "The LOCAL IP/DNS of your SmartLinc or 2422 Hub")
    input("port", "text", title: "Port", description: "The port, typically 25105")
    input("username", "text", title: "Username", description: "The username (set in your insteon settings)")
    input("password", "password", title: "Password", description: "The password (set in your insteon settings)")
} 
 
metadata {
    definition (name: "Insteon", author: "DMaverock", oauth: true) {
        capability "Polling"
        capability "Switch"
        capability "Refresh"
        capability "Switch Level"
    }

    // simulator metadata
    simulator {
    }

    // UI tile definitions
    tiles(scale: 2) {
		multiAttributeTile(name: "switch", type: "lighting", width: 6, height: 4, canChangeIcon: true, canChangeBackground: true) {
			tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
        			attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn"
			      	attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.light.on", backgroundColor: "#79b821", nextState: "turningOff"
                  	attributeState "turningOff", label: '${name}', action: "switch.on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn"
			      	attributeState "turningOn", label: '${name}', action: "switch.off", icon: "st.switches.light.on", backgroundColor: "#79b821", nextState: "turningOff"
            		}
            		tileAttribute("device.level", key: "SLIDER_CONTROL") {
                  		attributeState "level", action:"switch level.setLevel"
            		}
            		tileAttribute("level", key: "SECONDARY_CONTROL") {
                  		attributeState "level", label: 'Light dimmed to ${currentValue}%'
            		}    
		}
		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 6, height: 2) {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
      
		main "switch"
		details(["switch","attDimRate", "refresh", "attDimOnOff"])
	}  
    
}

def on() {	
    log.debug "Switch On"
    sendCmd("11", "FF")            
    sendEvent(name: "switch",  value: "on");    
}

def off() {	
    log.debug "Switch Off"
    sendCmd("13", "00")        
    sendEvent(name: "switch", value: "off");    
}
/*
def on() {	
	log.debug "turning on switch"
    sendEvent(name: "switch", value: "on")
    sendEvent(name: "level", value: 100)    
    def netID = device.deviceNetworkId
    log.debug "netID is $netID"
    turnOn()
    device.deviceNetworkId = netID
}

def turnOn() {
	log.debug "turnOn()"
    def host = hostLocal  
    def hosthex = convertIPToHex(host)
    def porthex = Long.toHexString(Long.parseLong((port)))
    if (porthex.length() < 4) { porthex = "00" + porthex }

    log.debug "Port in Hex is $porthex"
    log.debug "Hosthex is : $hosthex"    
    def deviceNetworkId = "$hosthex:$porthex" 
    log.debug "deviceNetworkID is $deviceNetworkId"

	def path = "/3?0262" + "${deviceid}" + "0F11FF=I=3"
    log.debug "OnPath is: $path"
	
    def userpassascii = "${settings.username}:${settings.password}"
	def userpass = "Basic " + userpassascii.encodeAsBase64().toString()
    def headers = [:]
    headers.put("HOST", "$host:$port")
    headers.put("Authorization", userpass)    
   
    def method = "GET"
    //sendHubCommand(new physicalgraph.device.HubAction("""${method} ${path} HTTP/1.1\r\nHOST: ${settings.hostLocal}:${settings.port}\r\n\r\n""",physicalgraph.device.Protocol.LAN,"${deviceNetworkId}"))
	
    try {
    def hubAction = new physicalgraph.device.HubAction(
    	method: method,
    	path: path,
    	headers: headers,
        deviceNetworkID: deviceNetworkID
        )
    }
    catch (Exception e) {
    log.debug "Hit Exception on $hubAction"
    log.debug e
    }
    hubAction    
}

def off() {	

    sendEvent(name: "switch", value: "off")
    sendEvent(name: "level", value: 0)
    def netID = device.deviceNetworkId
    log.debug "netID is $netID"
    turnOff()
    device.deviceNetworkId = netID
}

def turnOff() {

    def host = hostLocal
	def hosthex = convertIPToHex(host)
    def porthex = Long.toHexString(Long.parseLong((port)))
    if (porthex.length() < 4) { porthex = "00" + porthex }

    //log.debug "Port in Hex is $porthex"
    //log.debug "Hosthex is : $hosthex"    
    //device.deviceNetworkId = "$hosthex:$porthex" 
    
	def path = "/3?0262" + "${deviceid}" + "0F1300=I=3"
    log.debug "OffPath is: $path"
	
    def userpassascii = "${settings.username}:${settings.password}"
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
        hubAction
    }
    catch (Exception e) {
    log.debug "Hit Exception on $hubAction"
    log.debug e
    }    	    
}
*/
def setLevel(level) {    
    def value = (level * 255 / 100)
    def lvl = hex(value)       
    log.debug "Dimming to " + level
    sendEvent(name: "level", value: level)
    sendCmd("21",lvl)
/*
    def netID = device.deviceNetworkId
    device.deviceNetworkId = lvl
    log.debug "BEFORE NetworkID is $netID"
    dim()
    device.deviceNetworkId = netID
    log.debug "AFTER NetworkID is $netID"
*/
}

def dim() {
	
    def host = hostLocal
    def lvl = device.deviceNetworkId
    log.debug "CURRENT NetworkID is $lvl"

	def path = "/3?0262" + "${deviceid}" + "0F21" + "${lvl}" + "=I=3"
    log.debug "DimPath is: $path"
	
    def userpassascii = "${settings.username}:${settings.password}"
	def userpass = "Basic " + userpassascii.encodeAsBase64().toString()
    def headers = [:] //"HOST:" 
    headers.put("HOST", "$host:$port")
    headers.put("Authorization", userpass)
    
    //log.debug "host is $host"
    //log.debug "userpass is ${settings.username} : ${settings.password}"   
	
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

def sendCmd(num, level)
{
    log.debug "in sendcmd"    
/*
    //def host = "192.168.0.200" 
	def path = "/3?0262" + "${settings.deviceid}"  + "0F" + "$num" + "$level" + "=I=3"
    log.debug "path is: $path"
    
    def userpassascii = "${settings.username}:${settings.password}"
	def userpass = "Basic " + userpassascii.encodeAsBase64().toString()
    
    def method = "GET"
    def headers = [:]
    headers.put("HOST", "${settings.hostLocal}:${settings.port}")
    headers.put("Authorization", userpass)
    //log.debug "method is $method"
    //log.debug "headers is $headers"
    
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
*/
    httpGet("http://${settings.username}:${settings.password}@${settings.host}:${settings.port}//3?0262${settings.deviceid}0F${num}${level}=I=3") {response ->     
        def content = response.data
        log.debug content
    } 

    log.debug "Command Sent: " + num + ", " + level
   
    def i = Math.round(convertHexToInt(level) / 256 * 100 )
	sendEvent( name: "level", value: i )
    
}

def refresh() {
	log.debug "Refreshing Light Status"
    poll()
}

def poll()
{
    sendCmd("19", "00")
/*
    def host = hostLocal
    def lvl = device.deviceNetworkId
    log.debug "CURRENT NetworkID is $lvl"

	def path = "/3?0262" + "${deviceid}" + "0F1900=I=3"
    log.debug "DimPath is: $path"
	
    def userpassascii = "${settings.username}:${settings.password}"
	def userpass = "Basic " + userpassascii.encodeAsBase64().toString()
    def headers = [:] //"HOST:" 
    headers.put("HOST", "$host:$port")
    headers.put("Authorization", userpass)
    
    //log.debug "host is $host"
    //log.debug "userpass is ${settings.username} : ${settings.password}"   
	
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
*/
    getStatus(1)
}

def initialize(){
    def freq = 1
    schedule("0 0/$freq * * * ?", refresh)
    log.debug "Initialize"
}

def getStatus(num) {
	if(num < 6)
    {
		httpGet("http://${settings.username}:${settings.password}@${settings.host}:${settings.port}/buffstatus.xml") {response ->             
        def content = response.data
        log.debug content
        
        if(content.text().length() == 100)
        {
            log.debug content.text().substring(22,28)
            if(content.text().substring(22,28) == settings.deviceid)
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
                sendCmd("19", "00")
                num = num + 1
                getStatus(num)
                log.debug "DeviceID is different"
            }
        }
        else
        {
            sendCmd("19", "00")        
            num = num + 1
            getStatus(num)
            log.debug "Unexpected Buffer Length (should be 100)"
        }
      }
   }
   else { log.debug "Timeout, too many retries (5)" }        
}

private Long converIntToLong(ipAddress) {
	long result = 0
	def parts = ipAddress.split("\\.")
    for (int i = 3; i >= 0; i--) {
        result |= (Long.parseLong(parts[3 - i]) << (i * 8));
    }

    return result & 0xFFFFFFFF;
}

private String convertIPToHex(ipAddress) {
	return Long.toHexString(converIntToLong(ipAddress));
}

private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}
private String convertHexToIP(hex) {
log.debug("Convert hex to ip: $hex") //	a0 00 01 6
	[convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

private getHostAddress() {
	def parts = device.deviceNetworkId.split(":")
    log.debug device.deviceNetworkId
	def ip = convertHexToIP(parts[0])
	def port = convertHexToInt(parts[1])
	return ip + ":" + port
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
    
    log.debug "status is: $status"

}