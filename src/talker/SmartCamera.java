package talker;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import osrs.Helper;

import java.util.Random;
import java.util.concurrent.Callable;

public class SmartCamera {
    ClientContext ctx;
    boolean _360 = false;
    int mainAngle = 0;

    public SmartCamera(ClientContext ctx) {
        this.ctx = ctx;
//        this.mainAngle = this.ctx.camera.angleTo()
    }

    public void rotate360Camera(boolean _360) {
        if (_360) {
            System.out.println("Rotating: " + this.ctx.camera.yaw());
            this.ctx.camera.angle('e');
            System.out.println("Current X   : " + ctx.camera.x());
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    System.out.println("Rotating: " + ctx.camera.yaw());
                    mainAngle += new Random().nextInt(30) + 10;
                    ctx.camera.angle(mainAngle);
                    if (mainAngle % 2 == 0)
                        moveDownCamera();
                    else
                        moveUpCamera();

                    if (ctx.controller.isStopping()) {
                        ctx.controller.stop();
                        return true;
                    }

                    return mainAngle > 350;
                }
            }, Helper.smartSecondsGen(), 10);

        } else {
            System.out.println("Rotating: " + ctx.camera.yaw());

        }
    }

    public void moveDownCamera() {
        System.out.println("Moving all the way down");
        this.ctx.camera.pitch(false);
    }

    public void moveUpCamera() {
        System.out.println("Moving all the way UP");
        this.ctx.camera.pitch(true);
    }
}
