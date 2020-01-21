package lib;

import caster.assets.WORLDS;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.World;
import org.powerbot.script.rt4.Worlds;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldUtil extends Worlds {
    List<List<Object>> avoidChunkList;
    List<Object> avoidWorldList;

    public WorldUtil(ClientContext ctx) {
        super(ctx);
        avoidChunkList = new ArrayList<>();
        avoidWorldList = new ArrayList<>();
    }

    public List<World> getCompleteList() {
        return this.get();
    }

    public List<World> getF2pJoinableList() {
        Stream<World> worldStream = this.get().stream().filter(world -> {
            return world.type().equals(World.Type.FREE) && !world.type().equals(World.Type.PVP) && world.type() != World.Type.DEAD_MAN && world.specialty() != World.Specialty.PVP && world.textColor() != 8355711;
        });

        return worldStream.collect(Collectors.toList());
    }

    public List<World> getP2pJoinableList() {
        Stream<World> worldStream = this.get().stream().filter(world -> {
            return !world.type().equals(World.Type.FREE) && !world.type().equals(World.Type.PVP) && world.type() != World.Type.DEAD_MAN && world.specialty() != World.Specialty.PVP && world.textColor() != 8355711;
        });

        return worldStream.collect(Collectors.toList());
    }

    public void cacheWorlds() {
        String f2pWorlds = this.getF2pJoinableList().stream().map(world -> world.id() + ",").collect(Collectors.joining());
        String p2pWorlds = this.getP2pJoinableList().stream().map(world -> world.id() + ",").collect(Collectors.joining());

        try {
            FileUtil.writeTo(ctx, "f2p.worlds", f2pWorlds);
            FileUtil.writeTo(ctx, "p2p.worlds", p2pWorlds);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void hop(int id) {
        ctx.worlds.select().id(id).poll().hop();
    }

    public List<String> shuffleWorlds() {
        Stream<String> st = Arrays.stream(WORLDS.f2pWorlds.split(","));
        List<String> worlds = st.filter(id -> !id.isEmpty()).collect(Collectors.toList());

        Collections.shuffle(worlds);
//        System.out.println(Arrays.toString(worlds.toArray()));
        return worlds;
    }

    public List<List<Object>> shuffleWorldsInChunks(int partSize) {
        Stream<String> worldIds = Arrays.stream(WORLDS.f2pWorlds.split(",")).filter(id -> !id.isEmpty());

        List<String> worlds = worldIds.collect(Collectors.toList());

        List<List<Object>> chunkList = new ArrayList<>();

        for (int i = 0; i < worlds.size(); i += partSize) {
            chunkList.add(Arrays.asList(Arrays.copyOfRange(worlds.toArray(), i, Math.min(i + partSize, worlds.size()))));
        }

        Collections.shuffle(chunkList);

        return chunkList;
    }

    public String peekWorldFrom(List<List<Object>> worldChunks) {
        Stack<List<Object>> worldChunk = new Stack<>();
        Stack<Object> worlds = new Stack<>();

        worldChunk.addAll(worldChunks);
        worlds.addAll(worldChunk.pop());

        return (String) worlds.pop();
    }

    public List<List<Object>> insertIntoAvoidList(Object worldId, int size) {
        if (this.avoidWorldList.size() < size)
            this.avoidWorldList.add(worldId);
        else {
            this.avoidChunkList.add(this.avoidWorldList);
            this.avoidWorldList = new ArrayList<>();
        }

        return avoidChunkList;
    }

}
