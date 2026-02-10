import asyncio
import json
import random
import time
import websockets

HOST = "0.0.0.0"
PORT = 8765

GATES = ["NORTE", "SUR", "ESTE", "OESTE"]

# Distribución de camisetas:
# - BLUE: 30%
# - MULTICOLOR: 10%
# - Otros: 60%
SHIRT_COLORS = (
    ["BLUE"] * 3 +
    ["MULTICOLOR"] * 1 +
    ["RED", "GREEN", "BLACK", "WHITE"] * 6
)

EVENT_INTERVAL_RANGE = (0.3, 1.2)  # segundos


def generate_entry_event() -> dict:
    return {
        "type": "ENTRY",
        "timestamp": int(time.time()),
        "gate": random.choice(GATES),
        "shirtColor": random.choice(SHIRT_COLORS)
    }


async def event_producer(websocket):
    try:
        while True:
            event = generate_entry_event()
            await websocket.send(json.dumps(event))
            await asyncio.sleep(random.uniform(*EVENT_INTERVAL_RANGE))
    except websockets.exceptions.ConnectionClosed:
        print("🔌 Cliente desconectado")


async def handler(websocket):
    print("✅ Cliente suscrito")
    await event_producer(websocket)


async def main():
    print(f"🚀 WebSocket server iniciado en ws://{HOST}:{PORT}")
    async with websockets.serve(handler, HOST, PORT):
        await asyncio.Future()  # run forever


if __name__ == "__main__":
    asyncio.run(main())
