import React, {useEffect, useState} from 'react';
import useWebSocket, {ReadyState} from 'react-use-websocket';
import './App.css'

function App() {
    const {
        sendJsonMessage,
        lastJsonMessage,
        readyState
    } = useWebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/api", {
        shouldReconnect: () => true,
        reconnectInterval: 500, // ms
        reconnectAttempts: Number.MAX_SAFE_INTEGER
    });

    const [availableTags, setAvailableTags] = useState([]);
    const [availableActions, setAvailableActions] = useState([]);
    const [availableScenes, setAvailableScenes] = useState([]);
    const [availableDevices, setAvailableDevices] = useState([]);
    const [selectedTags, setSelectedTags] = useState([]);
    const [lastActionResult, setLastActionResult] = useState(null);


    useEffect(
        () => {
            if (!lastJsonMessage || !lastJsonMessage.type) {
                return;
            }
            switch (lastJsonMessage.type) {
                case "available-tags":
                    setAvailableTags(lastJsonMessage.tags)
                    break;
                case "available-scenes":
                    setAvailableScenes(lastJsonMessage.scenes)
                    break;
                case "available-actions":
                    setAvailableActions(lastJsonMessage.actions)
                    break;
                case "available-devices":
                    setAvailableDevices(lastJsonMessage.devices)
                    break;
                case "action-result":
                    setLastActionResult(lastJsonMessage);
                    break;
                default:
                    console.log(`Unknown message type: ${lastJsonMessage.type}`)
            }
        },
        [lastJsonMessage],
    );

    const toggleTag = (tag) => {
        let index = selectedTags.indexOf(tag);
        if (index > -1) {
            selectedTags.splice(index, 1)
        } else {
            selectedTags.push(tag)
        }
        setSelectedTags([...selectedTags])
        sendJsonMessage({type: "tags-selected", tags: [...selectedTags]})
    }

    const executeBatchAction = (action) => {
        sendJsonMessage({type: "execute-batch-action", action: action, tags: [...selectedTags]})
    }

    const executeDeviceAction = (device, action) => {
        sendJsonMessage({type: "execute-device-action", action: action, device: device})
    }

    const activateScene = (scene) => {
        sendJsonMessage({type: "activate-scene", scene: scene})
    }

    const connectionStatus = {
        [ReadyState.CONNECTING]: 'Connecting',
        [ReadyState.OPEN]: 'Open',
        [ReadyState.CLOSING]: 'Closing',
        [ReadyState.CLOSED]: 'Closed',
        [ReadyState.UNINSTANTIATED]: 'Not Instantiated',
    }[readyState];

    return (<>
        <h3>Batch</h3>
        <div>
            {availableTags.map(tag => <button type="button" key={tag} onClick={() => toggleTag(tag)}
                                              className={selectedTags.indexOf(tag) > -1 ? "toggle-button-active" : null}>{tag}</button>)}
        </div>
        <div style={{"marginTop": "1em"}}>
            {availableActions.map(action => <button type="button" key={action.id}
                                                    onClick={() => executeBatchAction(action.id)}>{action.displayName}</button>)}
        </div>
        <h3>Scenes</h3>
        {availableScenes.map(scene => <button type="button" key={scene}
                                              onClick={() => activateScene(scene)}>{scene}</button>)}
        <h3>Devices</h3>
        {
            availableDevices.map((device) => <div key={device.name}>
                <h3>{device.name}</h3>
                {device.availableActions.map(action => <button type="button"
                                                               key={device.name + "-" + action.id}
                                                               onClick={() => executeDeviceAction(device.name, action.id)}>{action.displayName}</button>)}
            </div>)
        }
        <h3>Status</h3>
        <div>Connection: {connectionStatus}</div>
        {lastActionResult &&
        <div>Last action: Successful={lastActionResult.successful} Failed={lastActionResult.failed}</div>}
    </>);
}

export default App;
