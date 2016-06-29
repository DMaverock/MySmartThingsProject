/**
*  Insteon Connect
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
    name: "Insteon Connect",
    namespace: "DMaverock",
    author: "DMaverock",
    description: "This smartapp installs the Insteon Connect App so you can add multiple Insteon Devices",
    category: "My Apps",
    iconUrl: "http://cdn.device-icons.smartthings.com/Lighting/light11-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Lighting/light11-icn@2x.png",
    iconX3Url: "http://cdn.device-icons.smartthings.com/Lighting/light11-icn@2x.png",
    singleInstance: true)


preferences {
    page(name: "mainPage", title: "Existing Insteon Device", install: true, uninstall: true) {
        if(state?.installed) {
            section("Add a New Insteon Device") {
                app "Insteon Dimmer Child", "DMaverock", "Insteon Dimmer Child", title: "New Insteon Dimmer Switch, Dimmer Plug, or Bulb", page: "mainPage", multiple: true, install: true
                app "Insteon On/Off Child", "DMaverock", "Insteon On/Off Child", title: "New Insteon On/Off Switch or On/Off Plug", page: "mainPage", multiple: true, install: true
            }
        } else {
            section("Initial Install") {
                paragraph "This smartapp installs the Insteon Device Connect App so you can add multiple Insteon devices. Click install / done then go to smartapps in the flyout menu and add new Insteon devices or edit existing Insteon devices."
            }
        }
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"

    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"

    unsubscribe()
    initialize()
}

def initialize() {
    state.installed = true
}