import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';
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

    const availableTags = useRef([]);
    const availableActions = useRef([]);
    const availableScenes = useRef([]);
    const availableDevices = useRef([]);
    const lastActionResult = useRef(null);

    const [selectedTags, setSelectedTags] = useState([]);

    availableTags.current = useMemo(
        () => lastJsonMessage && lastJsonMessage.type === "available-tags" ? lastJsonMessage.tags : availableTags.current,
        [lastJsonMessage]
    )

    availableScenes.current = useMemo(
        () => lastJsonMessage && lastJsonMessage.type === "available-scenes" ? lastJsonMessage.scenes : availableScenes.current,
        [lastJsonMessage]
    )

    availableActions.current = useMemo(
        () => lastJsonMessage && lastJsonMessage.type === "available-actions" ? lastJsonMessage.actions : availableActions.current,
        [lastJsonMessage]
    )

    availableDevices.current = useMemo(
        () => lastJsonMessage && lastJsonMessage.type === "available-devices" ? lastJsonMessage.devices : availableDevices.current,
        [lastJsonMessage]
    )

    lastActionResult.current = useMemo(
        () => lastJsonMessage && lastJsonMessage.type === "action-result" ? lastJsonMessage : lastActionResult.current,
        [lastJsonMessage]
    )

    const toggleTag = useCallback(tag => setSelectedTags(oldTags => {
        const newTags = [...oldTags]
        let index = oldTags.indexOf(tag);
        if (index > -1) {
            newTags.splice(index, 1)
        } else {
            newTags.push(tag)
        }
        return newTags
    }), [])

    useEffect(() => {
        sendJsonMessage({type: "tags-selected", tags: selectedTags})
    }, [sendJsonMessage, selectedTags])

    const executeBatchAction = useCallback((action) => {
        sendJsonMessage({type: "execute-batch-action", action: action, tags: [...selectedTags]})
    }, [sendJsonMessage, selectedTags])

    const executeDeviceAction = useCallback((device, action) => {
        sendJsonMessage({type: "execute-device-action", action: action, device: device})
    }, [sendJsonMessage])

    const activateScene = useCallback((scene) => {
        sendJsonMessage({type: "activate-scene", scene: scene})
    }, [sendJsonMessage])

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
            {availableTags.current.map(tag => <button type="button" key={tag} onClick={() => toggleTag(tag)}
                                                      className={selectedTags.indexOf(tag) > -1 ? "toggle-button-active" : null}>{tag}</button>)}
        </div>
        <div style={{"marginTop": "1em"}}>
            {availableActions.current.map(action => <button type="button" key={action.id}
                                                            onClick={() => executeBatchAction(action.id)}>{action.displayName}</button>)}
        </div>
        <h3>Scenes</h3>
        {availableScenes.current.map(scene => <button type="button" key={scene}
                                                      onClick={() => activateScene(scene)}>{scene}</button>)}
        <h3>Devices</h3>
        {
            availableDevices.current.map((device) => <div key={device.name}>
                <h3>{device.name}</h3>
                {device.availableActions.map(action => <button type="button"
                                                               key={device.name + "-" + action.id}
                                                               onClick={() => executeDeviceAction(device.name, action.id)}>{action.displayName}</button>)}
            </div>)
        }
        <h3>Status</h3>
        <div>Connection: {connectionStatus}</div>
        {lastActionResult.current &&
        <div>Last action:
            Successful={lastActionResult.current.successful} Failed={lastActionResult.current.failed}</div>}
    </>);
}

export default App;
