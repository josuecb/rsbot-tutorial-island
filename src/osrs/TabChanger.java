package osrs;

import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;

public class TabChanger {

    private ClientContext ctx;

    public TabChanger(ClientContext ctx) {
        this.ctx = ctx;
    }


    /**
     * @param tab: desired tab
     * @return true if changed and false if it didn't change anything
     */
    public boolean changeTo(Game.Tab tab) {
        if (!ctx.game.tab().equals(tab)) {
            ctx.game.tab(tab);
            System.out.print("Switching to: ");
            System.out.println(ctx.game.tab());
            return true;
        }
        return false;
    }
}
