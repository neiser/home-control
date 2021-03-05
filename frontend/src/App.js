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
        reconnectAttempts: Number.MAX_SAFE_INTEGER
    });

    const [availableTags, setAvailableTags] = useState([]);
    const [availableActions, setAvailableActions] = useState([]);
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
                case "available-actions":
                    setAvailableActions(lastJsonMessage.actions)
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

    const triggerAction = (action) => {
        sendJsonMessage({type: "action", action: action, tags: [...selectedTags]})
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
            {availableTags.map(tag => <button type="button" key={"tag-" + tag} onClick={() => toggleTag(tag)}
                                              className={selectedTags.indexOf(tag) > -1 ? "toggle-button-active" : null}>{tag}</button>)}
        </div>
        <div style={{"marginTop": "1em"}}>
            {availableActions.map(action => <button type="button" key={"action-" + action.id}
                                                    onClick={() => triggerAction(action.id)}>{action.displayName}</button>)}
        </div>
        <h3>Status</h3>
        <div>Connection: {connectionStatus}</div>
        {lastActionResult &&
        <div>Last action: Successful={lastActionResult.successful} Failed={lastActionResult.failed}</div>}
    </>);
}

export default App;
