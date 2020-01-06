package talker;

import org.powerbot.script.Condition;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import talker.assets.NPC;
import talker.tasks.TalkToEntityTask;

import java.util.ArrayList;
import java.util.List;

@Script.Manifest(
        name = "Talker",
        description = "Talk to any entity",
        properties = "client=4; author=Josue; topic=999;"
)
public class TalkingScript extends PollingScript<ClientContext> {
    List<talker.Task> taskList = new ArrayList<>();
    int taskCounts = 0;

    @Override
    public void start() {
        taskList.add(new TalkToEntityTask(ctx,osrs.assets.NPC.QUEST_GUIDE, 1));
//        taskList.add(new TalkToEntityTask(ctx, NPC.ADAM, 2));

        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void poll() {
        for (Task t : taskList) {
            if (t.activate()) {
                System.out.println("Task started: " + taskCounts++);
                t.execute();
                break;
            }
        }

//        SmartCamera sc = new SmartCamera(ctx);
//        sc.rotate360Camera(true);
//        Condition.sleep(10000);
    }
}
