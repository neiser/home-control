import React, {useEffect, useState} from 'react';
import useWebSocket, {ReadyState} from 'react-use-websocket';

function App() {
    const {
        sendJsonMessage,
        lastJsonMessage,
        readyState
    } = useWebSocket("ws://localhost:8080/api", {
        shouldReconnect: () => true,
        reconnectAttempts: Number.MAX_SAFE_INTEGER
    });

    const [availableTags, setAvailableTags] = useState([]);
    const [selectedTags, setSelectedTags] = useState(new Set());

    useEffect(
        () => {
            if (!lastJsonMessage || !lastJsonMessage.type) {
                return;
            }
            switch (lastJsonMessage.type) {
                case "config":
                    setAvailableTags(lastJsonMessage.tags)
                    break;
                case "tooltip":
                    // TODO Implement me
                    console.log(lastJsonMessage);
                    break;
                default:
                    console.log(`Unknown message type: ${lastJsonMessage.type}`)
            }
        },
        [lastJsonMessage],
    );

    const toggleTag = (tag) => {
        if (selectedTags.has(tag)) {
            selectedTags.delete(tag)
        } else {
            selectedTags.add(tag)
        }
        setSelectedTags(selectedTags)
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
        <div>Home Control: {connectionStatus}</div>
        <div>
            {availableTags.map(tag => <button key={tag} onClick={() => toggleTag(tag)}>{tag}</button>)}
        </div>
        <div>
            <button onClick={() => triggerAction("SHUTTER_DOWN")}>
                Shutter down
            </button>
            <button onClick={() => triggerAction("SHUTTER_UP")}>
                Shutter up
            </button>
        </div>
    </>);
}

export default App;
