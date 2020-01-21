package caster;

import caster.tasks.WorldHopperTask;
import lib.Task;
import lib.WorldUtil;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;

import java.util.ArrayList;
import java.util.List;

@Script.Manifest(name = "VCaster", description = "Varrok Caster", properties = "client=4; author=DevPro; topic=999;")
public class VarrokCaster extends PollingScript<ClientContext> {
    List<Task> taskList = new ArrayList<>();
    public static boolean lockTimer = false;

    @Override
    public void start() {
        WorldUtil worldUtil = new WorldUtil(ctx);
        super.start();
//        taskList.add(new PathWalker(ctx, PATH.varrokEastBankToWestBank));
        taskList.add(new WorldHopperTask(ctx, 10, worldUtil));
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void poll() {
//        System.out.println(FileUtil.lib);

//        WorldUtil wu = new WorldUtil(ctx);
//        for (List<Object> list : wu.shuffleWorldsInChunks(7)) {
//            System.out.println(list.toString());
//        }

//        System.out.println(ctx.client().getClientState());
//        wu.shuffleWorlds();

//        ctx.groundItems.poll().interact("Take");
//        Condition.sleep(10000);
//        System.out.println(Arrays.toString(pu.randomizeCoordinateTiles()));
//        ctx.controller.stop();

        for (Task t : taskList) {
            if (t.activate()) {
                System.out.println("new Task");
                t.execute();
                break;
            }
        }
    }
}
