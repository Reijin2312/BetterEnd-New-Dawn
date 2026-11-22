from pathlib import Path

import nbtlib


WORKSPACE = Path(__file__).resolve().parents[2]
REPOSITORIES = (
    "BetterEnd",
    "BetterEnd-Fabric-1.21.1",
    "BetterEnd-1.21.11",
    "BetterEnd-Fabric-1.21.11",
    "BetterEnd-26.1",
    "BetterEnd-Fabric-26.1",
    "BetterEnd-26.2",
    "BetterEnd-Fabric-26.2",
    "BetterEnd-26.3",
    "BetterEnd-Fabric-26.3",
)
TEMPLATE = Path("src/main/resources/data/betterend/structure/crashed_ship.nbt")
CONTAINER_BLOCKS = {"minecraft:chest", "minecraft:trapped_chest"}
LOOT_TABLE = "minecraft:chests/end_city_treasure"


def verify(repository: str) -> None:
    path = WORKSPACE / repository / TEMPLATE
    structure = nbtlib.load(path)
    container_states = {
        index
        for index, state in enumerate(structure["palette"])
        if str(state["Name"]) in CONTAINER_BLOCKS
    }
    containers = [block for block in structure["blocks"] if int(block["state"]) in container_states]
    if len(containers) != 2:
        raise AssertionError(f"{repository}: expected two ship containers, found {len(containers)}")

    for block in containers:
        data = block.get("nbt")
        if data is None or str(data.get("LootTable", "")) != LOOT_TABLE:
            raise AssertionError(f"{repository}: ship container has no {LOOT_TABLE} loot table")
        if "Items" in data:
            raise AssertionError(f"{repository}: ship container still embeds an Items list")
        if "LootTableSeed" in data:
            raise AssertionError(f"{repository}: ship container uses a fixed loot seed")


for repo in REPOSITORIES:
    verify(repo)
    print(f"PASS {repo}")
