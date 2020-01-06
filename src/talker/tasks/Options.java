package talker.tasks;

import org.powerbot.script.rt4.Chat;
import org.powerbot.script.rt4.ChatOption;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import osrs.Helper;

import java.util.*;

public class Options extends Chat {

    public Options(ClientContext ctx) {
        super(ctx);
    }

    public List<String> getList() {
        List<String> list = new ArrayList<>();
        for (ChatOption o : this.get())
            list.add(o.text());

        return list;
    }

    public int size() {
        return this.getList().size();
    }

    public String find(String contains) {
        for (ChatOption o : this.get()) {
            if (o.text().contains(contains)) {
                return o.text();
            }
        }

        return null;
    }

    public boolean empty() {
        return this.size() < 1;
    }

    public String getRandomOption() {
        List<Integer> opts = Helper.optionIndexGen(this.get().size());
        Collections.shuffle(opts);

        Stack<Integer> optionTasks = new Stack<Integer>();
        optionTasks.addAll(opts);

        if (!this.empty()) {
//            System.out.println(Arrays.toString(this.getList().toArray()));
            return this.get().get(optionTasks.pop()).text();
        } else {
            return null;
        }
    }
}
